package com.game.score.gameMessageHandlers

import com.game.score.MainActivity
import com.game.score.core.CompetitorInfoAllManager
import com.game.score.core.IGameMessageHandlerEx
import com.game.score.core.IGameMessageModel
import com.game.score.core.sendInUI
import com.game.score.models.xml.receive.CompetitorInfoAll
import com.game.score.models.xml.send.CompetitorInfoAllResponse
import com.game.score.ui.main.MainViewModel

object CompetitorInfoAllMessageHandler : IGameMessageHandlerEx {
    //region 处理消息
    /**
     * 处理消息
     */
    override fun handle(
        messageModel: IGameMessageModel,
        mainViewModel: MainViewModel,
        mainActivity: MainActivity
    ) {
        if (messageModel is CompetitorInfoAll) {
            //把收到的CompetitorInfoAll消息更新到视图模型与SD卡里的CompetitorInfoAll.xml
            CompetitorInfoAllManager.update(messageModel)

            //回应收到消息
            CompetitorInfoAllResponse().sendInUI()
        }
    }
    //endregion
}