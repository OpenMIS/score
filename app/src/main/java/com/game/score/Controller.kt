package com.game.score

import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.game.score.core.GameSettingsUtil
import com.game.score.core.NetworkUtil
import com.game.score.models.xml.receive.CompetitorInfoAll
import com.game.score.ui.main.MainViewModel

class Controller {
    companion object {
        //region 定位到第一条分数为空的记录上，并设置此记录为当前记录。
        /**
         * 定位到第1条分数为空的记录，并设置它为当前。
         */
        fun goToFirstEmptyAndSetCurrent(viewModel: MainViewModel, recyclerView: RecyclerView) {
            //region 定位到第一条分数为空的记录上，并设置此记录为当前记录。
            //【注意】indexOfFirst如果找不到对应的，会返回-1。
            val index =
                viewModel.currentCompetitorInfo.value?.Score?.indexOfFirst {
                    !arrayOf(
                        ScoreConsts.Attribute_F_0,
                        ScoreConsts.Attribute_F_Status,
                        ScoreConsts.Attribute_F_TotalScore
                    ).contains(it.ScoreID) && it.ScoreValue.isBlank()
                }

            if (index != null && index >= 0) {
                viewModel.currentScoreIndex.value = index
                viewModel.currentScore.value =
                    viewModel.currentCompetitorInfo.value!!.Score!![index]

                (recyclerView.layoutManager as LinearLayoutManager?)!!.scrollToPositionWithOffset(
                    index,
                    /*距离顶部的像素。通过此值，让正在打分的项尽量列表的上下的中间位置，
                    这样方便看到之前打分与之后要打的分。
                    */
                    100
                )
                recyclerView.adapter?.notifyDataSetChanged()
            }
            //endregion
        }
        //endregion

        //region 是否有Wifi网络
        /**
         * 是否有Wifi网络
         */
        fun isWifiNetworkAvailable(context: Context): Boolean {
            val result = NetworkUtil.isWifiNetworkAvailable(context)
            if (!result)
                Toast.makeText(
                    context,
                    context.getString(R.string.toast_noWifi),
                    Toast.LENGTH_SHORT
                ).show()

            return result
        }
        //endregion

        fun updateMainUI(viewModel: MainViewModel, mainActivity: MainActivity) {
            with(viewModel) {
                if (!haveABreak.value!! && competitorInfoAll.value != null &&
                    competitorInfoAll.value!!.CompetitorInfo.count() > 0
                ) {
                    if (currentCompetitorInfo.value == null)
                        currentCompetitorInfo.value = competitorInfoAll.value!!.CompetitorInfo[0]

                    competitorName_Normal.value = true
                    (currentCompetitorInfo.value!!.Event + currentCompetitorInfo.value!!.Phase).let {
                        if (it.isNotBlank()) eventAndPhase.value = it
                    }

                    currentCompetitorInfo.value!!.CompetitorName.let {
                        if (it.isNotBlank() ||
                            competitorName.value == mainActivity.getString(R.string.validate_success_competitorName)
                        )
                            competitorName.value = it
                    }

                    judgeName.value = currentCompetitorInfo.value!!.JudgeName

                    //region CompetitorInfo
                    var changeCompetitorInfoIndex = 0
                    if (currentCompetitorInfoIndex.value != null &&
                        currentCompetitorInfoIndex.value!! >= 0 &&
                        currentCompetitorInfoIndex.value!! < competitorInfoAll.value!!.CompetitorInfo.count()
                    )
                        changeCompetitorInfoIndex = currentCompetitorInfoIndex.value!!
                    else {
                        val temp = GameSettingsUtil.getCurrentCompetitorInfoIndex(mainActivity)
                        if (temp >= 0 &&
                            temp < competitorInfoAll.value!!.CompetitorInfo.count()
                        )
                            changeCompetitorInfoIndex = temp
                    }

                    val currentCompetitorInfoTemp =
                        competitorInfoAll.value!!.CompetitorInfo[changeCompetitorInfoIndex]

                    if (currentCompetitorInfoIndex.value != changeCompetitorInfoIndex)
                        currentCompetitorInfoIndex.value = changeCompetitorInfoIndex
                    //endregion

                    //region Score
                    if (currentCompetitorInfo.value!!.Score!!.count() > 0) {
                        var changeScoreIndex = 0
                        if (currentScoreIndex.value != null &&
                            currentScoreIndex.value!! >= 0 &&
                            currentScoreIndex.value!! < currentCompetitorInfo.value!!.Score!!.count()
                        )
                            changeScoreIndex = currentScoreIndex.value!!
                        else {
                            val temp = GameSettingsUtil.getCurrentScoreIndex(mainActivity)
                            if (temp >= 0 &&
                                temp < currentCompetitorInfo.value!!.Score!!.count()
                            )
                                changeScoreIndex = temp
                        }

                        currentScore.value = currentCompetitorInfo.value!!.Score!![changeScoreIndex]
                        if (currentScoreIndex.value != changeScoreIndex)
                            currentScoreIndex.value = changeScoreIndex
                    } else {
                        currentScore.value =
                            CompetitorInfoAll.CompetitorInfoClass.ScoreClass.emptyValueInstance
                        if (currentScoreIndex.value != -1)
                            currentScoreIndex.value = -1
                    }
                    //endregion

                    //【注意】必须先设置currentScoreIndex，因为下句会触发分数List绑定。 List绑定时，使用到currentScoreIndex。
                    currentCompetitorInfo.value = currentCompetitorInfoTemp
                }
            }
        }
    }
}
