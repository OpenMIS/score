package com.game.score

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.game.score.core.ExceptionHandlerUtil
import com.game.score.models.xml.receive.CompetitorInfo
import com.game.score.ui.main.ScoreListAdapter

/**
 * 显示分数列表
 */
@BindingAdapter("listData")
fun bindRecyclerView(
    recyclerView: RecyclerView,
    data: List<CompetitorInfo.CompetitorInfoClass.ScoreClass>?
) {
    ExceptionHandlerUtil.usingExceptionHandler {
        val adapter = recyclerView.adapter as ScoreListAdapter
        val filterData = data?.filter {
            it.ScoreID != "F_Status" //不显示ScoreID=F_Status的数据行。此行ScoreName一般为Validate。
        }
        adapter.submitList(filterData) {
            // scroll the list to the top after the diffs are calculated and posted
            recyclerView.scrollToPosition(0)
        }
    }
}