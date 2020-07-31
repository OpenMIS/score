package com.game.score.core

import android.util.Log

class MessageDistribute : GameMessageHandler {
    /**
     * 竞赛消息处理器
     */
    val _gameMessageHandlers = mutableListOf<GameMessageHandler>()

    /**
     * 处理消息
     */
    override fun Handle(messageModel: Any) {
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