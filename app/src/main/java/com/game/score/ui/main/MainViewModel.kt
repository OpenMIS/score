package com.game.score.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val athleteNameAndTeamName = MutableLiveData<String>("")
    val scoreString = MutableLiveData<String>("")
    val deviceCode = MutableLiveData<String>("") //E、C 等单个字符
}