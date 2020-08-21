package com.game.score.core

import com.game.score.MainActivity
import com.game.score.ui.main.MainViewModel

/**
 * 竞赛消息处理器扩展
 */
interface IGameMessageHandlerEx {

    /**
     * 处理消息
     */
    fun handle(
        messageModel: IGameMessageModel,
        mainViewModel: MainViewModel,
        mainActivity: MainActivity
    )
}