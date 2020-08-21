package com.game.score.gameMessageHandlers

import com.game.score.Controller
import com.game.score.MainActivity
import com.game.score.core.IGameMessageHandlerEx
import com.game.score.core.IGameMessageModel
import com.game.score.core.sendInUI
import com.game.score.models.xml.receive.CompetitorInfo
import com.game.score.models.xml.send.CompetitorInfoResponse
import com.game.score.ui.main.MainViewModel

object CompetitorInfoMessageHandler : IGameMessageHandlerEx {
    //region 处理消息
    /**
     * 处理消息
     */
    override fun handle(
        messageModel: IGameMessageModel,
        mainViewModel: MainViewModel,
        mainActivity: MainActivity
    ) {
        if (messageModel is CompetitorInfo) {
            with(mainViewModel) {
                if (messageModel.CompetitorInfo.CompetitorID.isBlank()) {//视为 服务端在打分器监控台 点击Break
                    //region 休息一下
                    Controller.haveABreak(mainViewModel, mainActivity) //休息一下
                    //endregion
                } else {
                    haveABreak.value = false
                    Controller.updateMainViewModel(
                        mainViewModel,
                        mainActivity
                    )
                }

                //回应收到消息
                CompetitorInfoResponse().sendInUI()
            }
        }
    }
    //endregion
}