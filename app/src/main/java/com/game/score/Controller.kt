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
        fun goToFirstEmptyScoreAndSetCurrent(viewModel: MainViewModel, recyclerView: RecyclerView) {
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

        //region 更新模型视图
        /**
         * 更新模型视图
         */
        fun updateMainViewModel(viewModel: MainViewModel, mainActivity: MainActivity) {
            with(viewModel) {
                if (!haveABreak.value!! && competitorInfoAll.value != null &&
                    competitorInfoAll.value!!.CompetitorInfo.count() > 0
                ) {
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

                    progress.value = String.format(
                        "%s/%s",
                        currentCompetitorInfoIndex.value!! + 1,
                        competitorInfoAll.value!!.CompetitorInfo.count()
                    )

                    //region Score
                    if (currentCompetitorInfoTemp.Score!!.count() > 0) {
                        var changeScoreIndex = 0
                        if (currentScoreIndex.value != null &&
                            currentScoreIndex.value!! >= 0 &&
                            currentScoreIndex.value!! < currentCompetitorInfoTemp.Score!!.count()
                        )
                            changeScoreIndex = currentScoreIndex.value!!
                        else {
                            val temp = GameSettingsUtil.getCurrentScoreIndex(mainActivity)
                            if (temp >= 0 &&
                                temp < currentCompetitorInfoTemp.Score!!.count()
                            )
                                changeScoreIndex = temp
                        }

                        currentScore.value = currentCompetitorInfoTemp.Score!![changeScoreIndex]
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

                    competitorName_Normal.value = true
                    //region 下面必须在currentCompetitorInfo.value设置后执行
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
                    //endregion
                }
            }
        }
        //endregion

        //region 下一人
        /**
         * 下一人
         */
        fun next(
            mainViewModel: MainViewModel,
            mainActivity: MainActivity,
            recyclerView: RecyclerView? = null,
            forDeleteCompetitorInfo: Boolean = false
        ): Boolean {
            var result = false
            val recyclerView2 = recyclerView ?: mainActivity.findViewById(R.id.score_list)
            with(mainViewModel.currentCompetitorInfoIndex) {
                if (mainViewModel.competitorInfoAll.value != null && value != null &&
                    value != -1 &&
                    value!! < mainViewModel.competitorInfoAll.value!!.CompetitorInfo.count()
                ) {
                    val count = mainViewModel.competitorInfoAll.value!!.CompetitorInfo.count()
                    if (!forDeleteCompetitorInfo)
                        value =
                            if (value!! == count - 1) 0
                            else value!! + 1

                    if (value!! == count - 1) {
                        Toast.makeText(
                            mainActivity,
                            mainActivity.getString(R.string.toast_end),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (value!! == 0) {
                        Toast.makeText(
                            mainActivity,
                            mainActivity.getString(R.string.toast_first),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    mainViewModel.currentCompetitorInfo.value =
                        mainViewModel.competitorInfoAll.value!!.CompetitorInfo[value!!]

                    updateMainViewModel(
                        mainViewModel,
                        mainActivity
                    )
                    goToFirstEmptyScoreAndSetCurrent(
                        mainViewModel,
                        recyclerView2
                    )

                    result = true
                }
            }

            return result
        }
        //endregion
    }
}
