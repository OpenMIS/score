package com.game.score.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
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
    val deviceCode = MutableLiveData<String>(" ") //E、C 等单个字符

    /**
     * 场次里的步骤。
     *
     * 比如：1 立定敬礼
     */
    val matchStep = MutableLiveData<String>("")

    /**
     * 打分。
     *
     * 分数字符串。
     */
    val scoreString = MutableLiveData<String>("")
}