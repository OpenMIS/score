package com.game.score

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.game.score.models.Score
import com.game.score.ui.main.ScoreListAdapter

/**
 * 显示分数列表
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Score>?) {
    val adapter = recyclerView.adapter as ScoreListAdapter
    adapter.submitList(data) {
        // scroll the list to the top after the diffs are calculated and posted
        recyclerView.scrollToPosition(0)
    }
}