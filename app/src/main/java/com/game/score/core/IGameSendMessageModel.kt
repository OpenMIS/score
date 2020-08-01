package com.game.score.core

/**
 * 竞赛用于发送的消息模型
 */
interface IGameSendMessageModel : IGameMessageModel {
    /**
     * 裁判ID。
     *
     * 实际数据：1-3。
     *
     * 与设置文件里的ClientID对应。
     *
     * 发送时自动设置此值。
     */
    var JudgeID: Int
}

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