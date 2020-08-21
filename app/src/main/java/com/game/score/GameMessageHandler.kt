package com.game.score

import com.game.score.core.ExceptionHandlerUtil
import com.game.score.core.IGameMessageHandler
import com.game.score.core.IGameMessageHandlerEx
import com.game.score.core.IGameMessageModel
import com.game.score.ui.main.MainViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.commons.lang3.time.DurationFormatUtils
import java.util.*

/**
 * 竞赛消息处理
 */
object GameMessageHandler : IGameMessageHandler {
    //region 字段
    private lateinit var _mainActivity: MainActivity
    private lateinit var _mainViewModel: MainViewModel
    var lastHeartBeat: Date? = null
    private var _startStatusCheck = false
    //endregion

    //region 工具集
    private fun startStatusCheckIfNeed() {
        if (!_startStatusCheck) {
            _startStatusCheck = true

            GlobalScope.launch {
                while (true) {
                    delay(3000L)
                    ExceptionHandlerUtil.usingExceptionHandler {
                        if (lastHeartBeat == null ||
                            DurationFormatUtils.formatPeriod(
                                lastHeartBeat!!.time,
                                Date().time,
                                "s"
                            )
                                .toInt() > 7 //7秒钟以上没有收到心跳回应包
                        )
                            _mainActivity.handler.sendEmptyMessage(1)
                    }
                }
            }
        }
    }
    //endregion
    //endregion

    //region 初始化
    /**
     * 初始化
     */
    fun init(
        appCompatActivity: MainActivity,
        mainViewModel: MainViewModel
    ) {
        _mainActivity = appCompatActivity
        _mainViewModel = mainViewModel

        startStatusCheckIfNeed()
    }
    //endregion

    //region 处理消息
    /**
     * 处理消息
     */
    override fun handle(messageModel: IGameMessageModel) {
        var messageHandler: IGameMessageHandlerEx? = null

        try {
            val messageHandlerClass = Class.forName(
                String.format(
                    "com.game.score.gameMessageHandlers.%sMessageHandler",
                    messageModel.javaClass.simpleName
                )
            )

            messageHandler = messageHandlerClass.kotlin.objectInstance as IGameMessageHandlerEx
        } catch (ex: ClassNotFoundException) {
        }

        messageHandler?.handle(messageModel, _mainViewModel, _mainActivity)
    }
    //endregion
}