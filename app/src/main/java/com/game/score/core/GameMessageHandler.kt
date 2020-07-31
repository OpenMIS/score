package com.game.score.core

/**
 * 竞赛消息处理器
 */
interface GameMessageHandler {

    /**
     * 处理消息
     */
    fun Handle(messageModel: Any)
}