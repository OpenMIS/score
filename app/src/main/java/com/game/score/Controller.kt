package com.game.score

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
                viewModel.competitorInfo.value?.CompetitorInfo?.Score?.indexOfFirst {
                    !arrayOf(
                        ScoreConsts.Attribute_F_0,
                        ScoreConsts.Attribute_F_Status,
                        ScoreConsts.Attribute_F_TotalScore
                    ).contains(it.ScoreID) && it.ScoreValue.isBlank()
                }

            if (index != null && index >= 0) {
                viewModel.currentScoreIndex.value = index
                viewModel.currentScore.value =
                    viewModel.competitorInfo.value!!.CompetitorInfo.Score!![index]

                (recyclerView.layoutManager as LinearLayoutManager?)!!.scrollToPositionWithOffset(
                    index,
                    /*距离顶部的像素。通过此值，让正在打分的项尽量列表的上下的中间位置，
                    这样方便看到之前打分与之后要打的分。
                    */
                    100
                )
            }
            //endregion
        }
        //endregion
    }
}