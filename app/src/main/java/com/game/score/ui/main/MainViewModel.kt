package com.game.score.ui.main

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.game.score.R
import com.game.score.core.IGameMessageHandler
import com.game.score.core.IGameMessageModel
import com.game.score.core.sendInUI
import com.game.score.models.xml.receive.CompetitorInfo
import com.game.score.models.xml.receive.ScoreResponse
import com.game.score.models.xml.send.CompetitorInfoResponse

class MainViewModel(application: Application) : IGameMessageHandler, AndroidViewModel(application) {

    private var _application: Application = application

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
     * 是否显示常规数据。
     *
     * true：是
     *
     * false：表示显示确认成绩成功的提示。
     */
    val eventAndPhase_Normal = MutableLiveData<Boolean>(false)

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
    val judgeName = MutableLiveData<String>("   ")

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
    val currentScore = MutableLiveData<CompetitorInfo.CompetitorInfoClass.ScoreClass>()
    //endregion

    //region 工具集
    //region 清除所有信息
    /**
     * 清除所有信息
     */
    private fun clearAll() {
        //【注意】此处不清除裁判名称
        eventAndPhase.value = null
        competitorName.value = null
        competitorInfo.value = null
        currentScoreIndex.value = -1
        currentScore.value = CompetitorInfo.CompetitorInfoClass.ScoreClass.emptyValueInstance
    }
    //endregion
    //endregion

    //region 处理消息
    /**
     * 处理消息
     */
    override fun handle(messageModel: IGameMessageModel) {
        if (messageModel is CompetitorInfo) {
            //region CompetitorInfo消息处理
            if (messageModel.CompetitorInfo.Score != null &&
                messageModel.CompetitorInfo.Score!!.count() > 0
            ) {//服务端发来带分数列表的xml视为需要打分操作
                //当裁判本场被打分完时，先保存其他场需要的打分

                competitorInfo.value = messageModel
                eventAndPhase_Normal.value = true
                (messageModel.CompetitorInfo.Event + messageModel.CompetitorInfo.Phase).let {
                    if (it.isNotBlank() ||
                        eventAndPhase.value == _application.getString(R.string.validate_success_eventAndPhase)
                    )
                        eventAndPhase.value = it
                }

                messageModel.CompetitorInfo.CompetitorName.let {
                    if (it.isNotBlank()) competitorName.value = it
                }

                judgeName.value = messageModel.CompetitorInfo.JudgeName

                var changeIndex = 0
                if (currentScoreIndex.value != null &&
                    currentScoreIndex.value!! >= 0 &&
                    currentScoreIndex.value!! < messageModel.CompetitorInfo.Score!!.count()
                )
                    changeIndex = currentScoreIndex.value!!

                currentScore.value = messageModel.CompetitorInfo.Score!![changeIndex]
                currentScoreIndex.value = changeIndex
            }
            //endregion

            //回应收到消息
            CompetitorInfoResponse().sendInUI()
        } else if (messageModel is ScoreResponse) {
            //region ScoreResponse消息处理
            if (competitorInfo.value != null &&
                competitorInfo.value!!.CompetitorInfo.Score != null &&
                competitorInfo.value!!.CompetitorInfo.CompetitorID ==
                messageModel.CompetitorInfo.CompetitorID
            ) {//同一个运动员 或者 同一组（多人项目）
                var change = false
                messageModel.CompetitorInfo.Score?.forEach { score ->
                    competitorInfo.value!!.CompetitorInfo.Score!!.find {
                        it.ScoreID == score.ScoreID
                    }?.let {
                        if (it.ScoreStatus != score.ScoreStatus) {
                            it.ScoreStatus = score.ScoreStatus
                            change = true
                        }

                        if (it.ScoreErrorMessage != score.ScoreErrorMessage) {
                            it.ScoreErrorMessage = score.ScoreErrorMessage
                            change = true
                        }

                        //【注意】由于服务端回应的分数可能为空的，所以需要判断一下。
                        if (!score.ScoreValue.isBlank() && it.ScoreValue != score.ScoreValue) {
                            it.ScoreValue = score.ScoreValue
                            change = true
                        }
                    }
                }

                if (change && scoreListChangeListener != null) {
                    scoreListChangeListener!!.invoke(this)
                }

                //region 确认成绩的回应
                val validateRowInApp = competitorInfo.value?.CompetitorInfo?.Score?.find {
                    it.ScoreID == "F_Status" && it.ScoreValue == "1"
                }

                if (validateRowInApp != null) {
                    if (messageModel.CompetitorInfo.Score == null) {//服务端确认成绩成功
                        clearAll() //清除所有信息
                        eventAndPhase_Normal.value = false //表示在eventAndPhase文本框显示“服务端确认成绩成功”相关消息

                        eventAndPhase.value =
                            _application.getString(R.string.validate_success_eventAndPhase)

                        Toast.makeText(
                            _application,
                            R.string.validate_success,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {//服务端确认成绩失败
                        validateRowInApp.ScoreValue = "" //清空确认，表示未确认成绩。

                        Toast.makeText(
                            _application,
                            R.string.validate_Fail,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                //endregion
            }
            //endregion
        }
    }
    //endregion
}