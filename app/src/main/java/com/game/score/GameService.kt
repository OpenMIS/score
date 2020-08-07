package com.game.score

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.game.score.core.*
import com.game.score.models.xml.receive.ScoreResponse
import com.game.score.ui.main.dummy.ScoreContent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class GameService : Service() {

    //region 字段
    /**
     * 接收到未处理的消息
     */
    val messages = LinkedList<IGameMessageModel>()

    private lateinit var _gameBinder: GameBinder

    /**
     * 竞赛使用的udp
     */
    private lateinit var _gameUdp: GameUdp

    /**
     * 竞赛消息处理器
     */
    private var _gameMessageHandler: IGameMessageHandler? = null

    /**
     * 竞赛消息处理器
     */
    val gameMessageHandler: IGameMessageHandler?
        get() = _gameMessageHandler
    //endregion

    //region 复写方法
    override fun onCreate() {
        ExceptionHandlerUtil.usingExceptionHandler {
            _gameBinder = GameBinder(this)
            super.onCreate()

            GameSettingsUtil.loadSettings(this) //载入设置
            _gameUdp = GameUdp.initForService(this) //初始化（为本地Android服务）
            _gameUdp.startReceiveThread() //启动UDP数据接收线程

            //region 如果是Google模拟器，模拟收到XML消息。
            if (Build.PRODUCT == "sdk_gphone_x86_64") {
                //模拟收到一个CompetitorInfo消息
                messages.push(ScoreContent.competitorInfo)

                GlobalScope.launch { // 在后台启动一个新的协程并继续
                    //region 模拟改分
                    for (i in 2..4) {
                        delay(3000L) // 非阻塞的等待 3 秒钟（默认时间单位是毫秒）
                        gameMessageHandler?.handle(
                            ScoreResponse(
                                "ScoreResponse",
                                ScoreResponse.CompetitorInfoClass(
                                    "1",
                                    mutableListOf(
                                        ScoreResponse.CompetitorInfoClass.ScoreClass(
                                            "F_$i",
                                            "$i",
                                            "",
                                            ""
                                        )
                                    )
                                )
                            )
                        )
                    }
                    //endregion

                    for (i in 5..6) {
                        delay(3000L) // 非阻塞的等待 1 秒钟（默认时间单位是毫秒）
                        gameMessageHandler?.handle(
                            ScoreResponse(
                                "ScoreResponse",
                                ScoreResponse.CompetitorInfoClass(
                                    "1",
                                    mutableListOf(
                                        ScoreResponse.CompetitorInfoClass.ScoreClass(
                                            "F_$i",
                                            "$i",
                                            ScoreConsts.ScoreStatus_Error,
                                            "Score out of Range"
                                        )
                                    )
                                )
                            )
                        )
                    }
                }
                //endregion
            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        _gameMessageHandler = null
        return super.onUnbind(intent)
    }

    override fun onBind(intent: Intent): IBinder {
        return _gameBinder
    }
    //endregion

    //region 设置竞赛消息处理器
    /**
     * 设置竞赛消息处理器
     *
     * @param gameMessageHandler 竞赛消息处理器
     */
    fun setGameMessageHandler(gameMessageHandler: IGameMessageHandler) {
        //region 处理已经收到的消息
        var messageModel: IGameMessageModel? = null
        while (messages.count() > 0) {
            ExceptionHandlerUtil.usingExceptionHandler(
                """处理消息时错误。消息内容如下：
$messageModel
""".trimMargin()
            ) {
                messageModel = messages.pop()
                if (messageModel != null)
                    gameMessageHandler.handle(messageModel!!)
            }
        }
        //endregion

        _gameMessageHandler = gameMessageHandler
    }
    //endregion

    //region 重启接收消息线程
    /**
     *  重启接收消息线程
     */
    fun restartReceiveThread() {
        _gameUdp.stopReceive() //停止接收线程
        _gameUdp.startReceiveThread() //启动UDP数据接收线程
    }
    //endregion

    class GameBinder(val service: GameService) : Binder()
}
