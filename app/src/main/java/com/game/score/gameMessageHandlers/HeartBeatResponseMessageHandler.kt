package com.game.score.gameMessageHandlers

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.game.score.GameMessageHandler
import com.game.score.MainActivity
import com.game.score.R
import com.game.score.core.IGameMessageHandlerEx
import com.game.score.core.IGameMessageModel
import com.game.score.models.xml.receive.HeartBeatResponse
import com.game.score.ui.main.MainViewModel
import java.util.*

object HeartBeatResponseMessageHandler : IGameMessageHandlerEx {
    //region 处理消息
    /**
     * 处理消息
     */
    override fun handle(
        messageModel: IGameMessageModel,
        mainViewModel: MainViewModel,
        mainActivity: MainActivity
    ) {
        if (messageModel is HeartBeatResponse) {
            GameMessageHandler.lastHeartBeat = Date()

            with(mainActivity.findViewById<TextView>(R.id.textView_appStatus)) {
                mainViewModel.appStatus.value = mainActivity.getString(
                    R.string.app_status_online
                )
                setTextColor(ContextCompat.getColor(mainActivity, R.color.colorAppStatus_Online))
            }
        }
    }
    //endregion
}