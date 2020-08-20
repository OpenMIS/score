package com.game.score.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.game.score.models.xml.receive.CompetitorInfoAll

class MainViewModel(application: Application) : AndroidViewModel(application) {

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
     * 运动员与队名。
     *
     * 比如：贾海涛(浙江队)
     */
    val competitorName = MutableLiveData<String>("")

    /**
     * 是否显示常规数据。
     *
     * true：是
     *
     * false：表示显示确认成绩成功的提示。
     */
    val competitorName_Normal = MutableLiveData<Boolean>(false)

    /**
     * 裁判位置代码。
     *
     * E、M、C 等单个字符。
     *
     * 【注意】此处默认必须为空格，否则在初始化时，无法长按进入“设置”页面。
     */
    val judgeName = MutableLiveData<String>("   ")

    /**
     * App状态。
     *
     * 在线/离线
     */
    val appStatus = MutableLiveData<String>()

    /**
     * 当前是否在“休息一下”状态
     */
    val haveABreak = MutableLiveData(false)

    /**
     * CompetitorInfoAll 数据模型
     *
     */
    val competitorInfoAll = MutableLiveData<CompetitorInfoAll>()

    /**
     * 当前CompetitorInfo 在 CompetitorInfoAll.CompetitorInfo列表里的索引
     *
     */
    val currentCompetitorInfoIndex = MutableLiveData<Int>()

    /**
     * CompetitorInfoAll 数据模型里的 CompetitorInfoClass 类
     *
     */
    val currentCompetitorInfo = MutableLiveData<CompetitorInfoAll.CompetitorInfoClass>()

    /**
     * 当前分数 在 分数列表里的索引
     *
     */
    val currentScoreIndex = MutableLiveData<Int>()

    /**
     * 当前分数模型
     *
     */
    val currentScore = MutableLiveData<CompetitorInfoAll.CompetitorInfoClass.ScoreClass>()
    //endregion

    //region 清除所有信息
    /**
     * 清除所有信息
     */
    fun clearAll(clearCompetitorInfoAll: Boolean = true) {
        //【注意】此处不清除裁判名称
        eventAndPhase.value = null
        competitorName.value = null
        if (clearCompetitorInfoAll)
            competitorInfoAll.value = null
        currentCompetitorInfo.value = null
        currentCompetitorInfoIndex.value = -1
        currentScoreIndex.value = -1
        currentScore.value = CompetitorInfoAll.CompetitorInfoClass.ScoreClass.emptyValueInstance
    }
    //endregion
}