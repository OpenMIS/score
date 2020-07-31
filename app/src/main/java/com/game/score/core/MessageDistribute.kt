package com.game.score.core

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log

class MessageDistribute : GameMessageHandler {
    //region 字段
    /**
     * 竞赛消息处理器
     */
    private val _gameMessageHandlers = mutableListOf<GameMessageHandler>()

    private val _handler = object : Handler(Looper.getMainLooper()) {
        /*
         * handleMessage() defines the operations to perform when
         * the Handler receives a new Message to process.
         */
        override fun handleMessage(inputMessage: Message) {//在界面线程处理
            // Gets the image task from the incoming Message object.
            val messageModel = inputMessage.obj as GameMessageModel

            //region 分发消息
            _gameMessageHandlers.forEach {
                try {
                    it.Handle(messageModel)
                } catch (e2: Exception) {
                    Log.e(
                        javaClass.simpleName, """处理消息时错误。消息内容如下：
$messageModel
""".trimMargin(), e2
                    )
                }
            }
            //endregion
        }
    }
    //endregion

    /**
     * 处理消息
     */
    override fun Handle(messageModel: GameMessageModel) {
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
        fun registerGameMessageHandlerIfNeed(gameMessageHandler: GameMessageHandler) {
            if (!instance._gameMessageHandlers.contains(gameMessageHandler))
                instance._gameMessageHandlers.add(gameMessageHandler)
        }
    }
}