package com.game.score.core

import android.os.Handler
import android.os.Looper
import android.os.Message

class MessageDistribute : IGameMessageHandler {
    //region 字段
    /**
     * 竞赛消息处理器
     */
    private val _gameMessageHandlers = mutableListOf<IGameMessageHandler>()

    private val _handler = object : Handler(Looper.getMainLooper()) {
        /*
         * handleMessage() defines the operations to perform when
         * the Handler receives a new Message to process.
         */
        override fun handleMessage(inputMessage: Message) {//在界面线程处理
            ExceptionHandlerUtil.usingExceptionHandler {
                // Gets the image task from the incoming Message object.
                val messageModel = inputMessage.obj as IGameMessageModel

                //region 分发消息
                _gameMessageHandlers.forEach {
                    ExceptionHandlerUtil.usingExceptionHandler(
                        """处理消息时错误。消息内容如下：
$messageModel
""".trimMargin()
                    ) {
                        it.handle(messageModel)
                    }
                }
                //endregion
            }
        }
    }
    //endregion

    /**
     * 处理消息
     */
    override fun handle(messageModel: IGameMessageModel) {
        //从非界面线程发送消息到界面线程
        _handler.sendMessage(Message().apply { obj = messageModel })
    }

    companion object {
        /**
         * 实例（单件）
         */
        val instance = MessageDistribute()

        /**
         * 注册竞赛消息处理器（如果需要的话）
         */
        fun registerGameMessageHandlerIfNeed(gameMessageHandler: IGameMessageHandler) {
            if (!instance._gameMessageHandlers.contains(gameMessageHandler))
                instance._gameMessageHandlers.add(gameMessageHandler)
        }
    }
}