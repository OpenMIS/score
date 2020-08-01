package com.game.score.core

/**
 * 竞赛消息处理器
 */
interface IGameMessageHandler {

    /**
     * 处理消息
     */
    fun handle(messageModel: IGameMessageModel)
}