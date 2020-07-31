package com.game.score.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.game.score.core.GameMessageHandler
import com.game.score.core.GameMessageModel
import com.game.score.models.xml.receive.CompetitorInfo
import com.game.score.models.xml.receive.ScoreResponse

class MainViewModel : GameMessageHandler, ViewModel() {
    /**
     * 分数列表改变
     */
    var scoreListChangeListener: ((MainViewModel) -> Unit)? = null

    /**
     * 场次与轮次。
     *
     * 比如：盛装舞步个人赛资格赛
     */
    val eventAndPhase = MutableLiveData<String>("")

    /**
     * 运动员与队名。
     *
     * 比如：贾海涛(浙江队)
     */
    val competitorName = MutableLiveData<String>("")

    /**
     * 裁判位置代码。
     *
     * E、M、C 等单个字符。
     *
     * 【注意】此处默认必须为空格，否则在初始化时，无法长按进入“设置”页面。
     */
    val judgeName = MutableLiveData<String>(" ")

    /**
     * 场次里的步骤。
     *
     * 比如：立定敬礼
     */
    val matchStep = MutableLiveData<String>("")

    /**
     * CompetitorInfo 数据模型
     *
     */
    val competitorInfo = MutableLiveData<CompetitorInfo>()

    /**
     * 当前分数 在 分数列表里的索引
     *
     */
    val currentScoreIndex = MutableLiveData<Int>()

    /**
     * 当前分数模型
     *
     */
    val currentScore = MutableLiveData<CompetitorInfo.CompetitorInfoClass.Score>()
    val changeTimes = MutableLiveData(0)
    //region 处理消息
    /**
     * 处理消息
     */
    override fun Handle(messageModel: GameMessageModel) {
        if (messageModel is CompetitorInfo) {
            //region CompetitorInfo消息处理
            competitorInfo.value = messageModel
            eventAndPhase.value =
                messageModel.CompetitorInfo.Event + messageModel.CompetitorInfo.Phase
            judgeName.value = messageModel.CompetitorInfo.JudgeName
            competitorName.value = messageModel.CompetitorInfo.CompetitorName

            if (messageModel.CompetitorInfo.Scores.count() > 0) {
                currentScore.value = messageModel.CompetitorInfo.Scores[0]
                currentScoreIndex.value = 0
            } else {
                currentScore.value = CompetitorInfo.CompetitorInfoClass.Score.emptyValueInstance
                currentScoreIndex.value = -1
            }
            //endregion
        } else if (messageModel is ScoreResponse) {
            //region ScoreResponse消息处理
            if (competitorInfo.value != null &&
                competitorInfo.value!!.CompetitorInfo.CompetitorID ==
                messageModel.ScoreResponse.CompetitorID
            ) {//同一个运动员 或者 同一组（多人项目）
                var change = false
                messageModel.ScoreResponse.Scores.forEach { score ->
                    competitorInfo.value!!.CompetitorInfo.Scores.find {
                        it.ScoreID == score.ScoreID
                    }?.let {
                        it.ScoreStatus = score.ScoreStatus
                        it.ScoreErrorMessage = score.ScoreErrorMessage
                        it.ScoreValue = score.ScoreValue

                        change = true
                    }
                }

                if (change && scoreListChangeListener != null) {
                    //changeTimes.value = changeTimes.value!! + 1
                    scoreListChangeListener!!.invoke(this)
                }
            }
            //endregion
        }
    }
    //endregion
}