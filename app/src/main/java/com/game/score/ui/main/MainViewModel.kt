package com.game.score.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.game.score.core.GameMessageHandler
import com.game.score.models.xml.receive.CompetitorInfo
import com.game.score.models.xml.receive.competitorInfo.Score
import com.game.score.ui.main.dummy.ScoreContent

class MainViewModel : GameMessageHandler, ViewModel() {
    /**
     * 场次信息。
     *
     * 比如：盛装舞步个人赛资格赛
     */
    val gameMatch = MutableLiveData<String>("")

    /**
     * 运动员与队名。
     *
     * 比如：贾海涛(浙江队)
     */
    val athleteNameAndTeamName = MutableLiveData<String>("")

    /**
     * 设备代码。
     *
     * E、M、C 等单个字符。
     *
     * 【注意】此处默认必须为空格，否则在初始化时，无法长按进入“设置”页面。
     */
    val deviceCode = MutableLiveData<String>(" ")

    /**
     * 场次里的步骤。
     *
     * 比如：立定敬礼
     */
    val matchStep = MutableLiveData<String>("")

    /**
     * 分数列表
     *
     */
    val scores = MutableLiveData<MutableList<Score>>(ScoreContent.ITEMS)

    /**
     * 当前分数 在 分数列表里的索引
     *
     */
    val currentScoreIndex = MutableLiveData<Int>()

    /**
     * 当前分数
     *
     */
    val currentScore = MutableLiveData<Score>()

    //region 处理消息
    /**
     * 处理消息
     */
    override fun Handle(messageModel: Any) {
        Log.d("TAG", "Handle: " + messageModel.javaClass.simpleName)

        if (messageModel is CompetitorInfo) {
            //messageModel.
        }
    }
    //endregion
}