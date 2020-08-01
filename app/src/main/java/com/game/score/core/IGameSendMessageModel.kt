package com.game.score.core

/**
 * 竞赛用于发送的消息模型
 */
interface IGameSendMessageModel : IGameMessageModel

//region 扩展方法
/**
 * 发送消息。
 *
 * 在本地Android服务。
 */
fun IGameSendMessageModel.sendInService() {
    GameUdp.sendInService(this)
}

/**
 * 发送消息。
 *
 * 在界面。
 */
fun IGameSendMessageModel.sendInUI() {
    GameUdp.sendInUI(this)
}
//endregion