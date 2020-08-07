package com.game.score.core

import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import com.game.score.GameService
import com.game.score.core.ExceptionHandlerUtil.Companion.usingExceptionHandler
import com.game.score.models.GameSettings
import com.game.score.models.xml.send.HeartBeat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.concurrent.thread

/**
 * 竞赛使用的udp
 */
class GameUdp(private val _context: ContextWrapper) {

    //region 字段
    /**
     * 倾听状态
     */
    private var _listenStatus = false

    /**
     * 用于接收消息的DatagramSocket（线程安全）
     */
    private var _receiveSocket: DatagramSocket? = null

    /**
     * 用于接收消息的线程
     */
    private var _receiveSocketThread: Thread? = null

    /**
     * 用于发送消息的DatagramSocket（线程安全）
     */
    private var _sendSocket: DatagramSocket = DatagramSocket()

    /**
     * 用于发送广播消息的DatagramSocket（线程安全）
     */
    private var _sendBroadcastSocket: DatagramSocket = DatagramSocket()

    //region 发送心跳的线程
    /**
     * 发送心跳的线程
     */
    private val _heartBeatThread: Thread = thread(false) {
        while (true) {
            try {
                usingExceptionHandler("发送心跳错误") {
                    //region 获取电量
                    var battery = 0 //电量
                    val batteryStatus: Intent? =
                        IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
                            _context.registerReceiver(null, ifilter)
                        }

                    if (batteryStatus != null)
                        battery = batteryStatus.getIntExtra("level", 0)
                    //endregion

                    HeartBeat(
                        HeartBeat = HeartBeat.HeartBeatClass(
                            battery, GameSettings.current!!.network_local_port, "1.0"
                        )
                    ).sendInService()
                }
            } finally {
                /*【注意】此处间隔时间设置5秒或5秒以上，在OVR软件的打分监控台里会出现超时。
                设置4秒偶尔出现超时。设置3秒基本没有出现超时。
                */
                Thread.sleep(3000L) //每隔3秒执行
            }
        }
    }
    //endregion
    //endregion

    //region 启动UDP数据接收线程
    /**
     *  启动UDP数据接收线程
     */
    fun startReceiveThread() {
        _listenStatus = true

        _receiveSocketThread = thread {
            usingExceptionHandler("启动接收UDP线程错误") {
                if (_context is GameService) {

                    if (!_heartBeatThread.isAlive)
                        _heartBeatThread.start()

                    with(GameSettings.current!!) {
                        _receiveSocket = DatagramSocket(network_local_port)
                        while (_listenStatus) {
                            val serverAddr = InetAddress.getByName(network_server_host)
                            val inBuf = ByteArray(65535) //65535为UDP整包的最大长度
                            val inPacket = DatagramPacket(inBuf, inBuf.size)
                            _receiveSocket!!.receive(inPacket)
                            if (inPacket.address != serverAddr) {
                                LogUtil.logger.warn("从未知服务端[{}]收到报文", inPacket.address)
                            } else {
                                //收到的二进制转字符串
                                var xmlContent: String? = null
                                usingExceptionHandler("收到的二进制转字符串时错误") {
                                    with(inPacket) {
                                        xmlContent = String(data, 0, length, Charsets.UTF_8)
                                    }
                                }
                                //endregion

                                var messageType: String? = null

                                if (xmlContent != null) {
                                    //region 消息转换
                                    usingExceptionHandler(
                                        """获取消息类型错误。消息内容如下：
$xmlContent
""".trimMargin()
                                    ) {
                                        messageType = GameMessageUtil.getMessageType(xmlContent!!)
                                    }
                                    //endregion

                                    if (messageType != null) {
                                        var gameMessageModel: IGameMessageModel? = null
                                        //region 解析消息
                                        usingExceptionHandler(
                                            """解析消息时错误。消息内容如下：
$xmlContent
""".trimMargin()
                                        ) {
                                            with(XmlMappers) {
                                                val class1 =
                                                    Class.forName("com.game.score.models.xml.receive.$messageType")
                                                //动态类型
                                                gameMessageModel = receive.readValue(
                                                    xmlContent,
                                                    class1
                                                ) as IGameMessageModel
                                            }
                                        }
                                        //endregion

                                        if (gameMessageModel != null) {
                                            //region 发送广播
                                            GlobalScope.launch {
                                                usingExceptionHandler("从服务端收到信息，转发UDP广播错误。") {
                                                    val broadcastAddressString =
                                                        getBroadcastAddress(network_server_host)
                                                    val broadcastAddress =
                                                        InetAddress.getByName(broadcastAddressString)
                                                    with(inPacket) {
                                                        val datagramPacket2 = DatagramPacket(
                                                            data,
                                                            0,
                                                            length,
                                                            broadcastAddress,
                                                            broadcastPort
                                                        )
                                                        _sendBroadcastSocket.send(datagramPacket2)
                                                    }
                                                }
                                            }
                                            //endregion

                                            if (_context.gameMessageHandler != null)
                                            //region 处理消息
                                                usingExceptionHandler(
                                                    """处理消息时错误。消息内容如下：
$xmlContent
""".trimMargin()
                                                ) {
                                                    _context.gameMessageHandler!!.handle(
                                                        gameMessageModel!!
                                                    )
                                                }
                                            //endregion
                                            else _context.messages.push(gameMessageModel)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    //endregion

    //region 停止接收线程
    /**
     * 停止接收线程
     */
    fun stopReceive() {
        _receiveSocket?.close()
        _receiveSocket = null

        _receiveSocketThread?.interrupt()
        _receiveSocketThread = null

        _listenStatus = false
    }
    //endregion

    companion object {
        //region 字段
        private var _forService: GameUdp? = null
        private var _forUI: GameUdp? = null

        /**
         * 广播端口
         */
        private const val broadcastPort = 8888
        //endregion

        //region 工具集
        //region 获取广播地址
        /**
         * 获取广播地址
         */
        private fun getBroadcastAddress(networkServerHost: String): String =
            networkServerHost.replace("""\.\d+$""".toRegex(), ".255")
        //endregion

        /**
         * 发送消息
         */
        @JvmStatic
        private fun send(
            datagramSocket: DatagramSocket,
            gameSendMessageModel: IGameSendMessageModel,
            datagramSocketForBroadcast: DatagramSocket
        ) {
            with(GameSettings.current!!) {
                gameSendMessageModel.JudgeID = client_id
                val xmlContent = XmlMappers.send.writeValueAsString(gameSendMessageModel)
                val bytes = xmlContent.toByteArray(Charsets.UTF_8)
                val serverAddr = InetAddress.getByName(network_server_host)

                val datagramPacket = DatagramPacket(
                    bytes,
                    0,
                    bytes.count(),
                    serverAddr,
                    network_server_port
                )

                //region 资料
                /*
Document\经验分享\技术\移动设备\移动开发\安卓\Android体系-经验.txt
24、从SDK3.0开始，google不再允许网络请求（HTTP、Socket）等相关操作直接在主线程中
Android 异常 android.os.NetworkOnMainThreadException https://blog.csdn.net/a78270528/article/details/47131683
【Android开发那点破事】解决android.os.NetworkOnMainThreadException https://blog.csdn.net/huxiweng/article/details/19908679
啰嗦一下android中的NetworkOnMainThreadException https://droidyue.com/blog/2014/11/08/look-into-android-dot-os-dot-networkonmainthreadexception/
                */
                //endregion
                GlobalScope.launch {
                    usingExceptionHandler("发送UDP消息错误") {
                        datagramSocket.send(datagramPacket)
                    }
                }

                //region 发送广播
                GlobalScope.launch {
                    usingExceptionHandler("发送UDP广播错误") {
                        val broadcastAddressString = getBroadcastAddress(network_server_host)
                        val broadcastAddress = InetAddress.getByName(broadcastAddressString)
                        val datagramPacket2 = DatagramPacket(
                            bytes,
                            0,
                            bytes.count(),
                            broadcastAddress,
                            broadcastPort
                        )
                        datagramSocketForBroadcast.send(datagramPacket2)
                    }
                }
                //endregion
            }
        }
        //endregion

        /**
         * 初始化（为界面）
         */
        @JvmStatic
        fun initForUI(_context: ContextWrapper): GameUdp {
            _forUI = GameUdp(_context)
            return _forUI!!
        }

        /**
         * 初始化（为本地Android服务）
         */
        @JvmStatic
        fun initForService(_context: ContextWrapper): GameUdp {
            _forService = GameUdp(_context)
            return _forService!!
        }

        //region 停止接收线程
        /**
         * 停止接收线程。
         *
         * 内部调用用于服务的实例的stopReceive方法。
         */
        @JvmStatic
        fun stopReceiveForGlobal() = _forService?.stopReceive()
        //endregion

        //region 发送消息（在本地Android服务）
        /**
         * 发送消息。
         *
         * 在本地Android服务。
         */
        @JvmStatic
        fun sendInService(gameSendMessageModel: IGameSendMessageModel) {
            if (_forService != null) {
                with(_forService!!) {
                    send(_sendSocket, gameSendMessageModel, _sendBroadcastSocket)
                }
            }
        }
        //endregion

        // region 发送消息（在界面）
        /**
         * 发送消息。
         *
         * 在界面。
         */
        @JvmStatic
        fun sendInUI(gameSendMessageModel: IGameSendMessageModel) {
            if (_forUI != null) {
                with(_forUI!!) {
                    send(_sendSocket, gameSendMessageModel, _sendBroadcastSocket)
                }
            }
        }
        //endregion
    }
}