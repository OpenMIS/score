package com.game.score.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.game.score.databinding.FragmentScoreItemBinding
import com.game.score.models.Score

/**
 * [RecyclerView.Adapter] that can display a [Score].
 */
class ScoreListAdapter(
    private val _clickListener: ScoreItemClickListener
) : ListAdapter<Score, ScoreListAdapter.ViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<Score>() {
        override fun areItemsTheSame(oldItem: Score, newItem: Score): Boolean {
            return oldItem.ScoreID == newItem.ScoreID
        }

        override fun areContentsTheSame(oldItem: Score, newItem: Score): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(_clickListener, getItem(position), position)

    class ViewHolder(private var _binding: FragmentScoreItemBinding) :
        RecyclerView.ViewHolder(_binding.root) {
        fun bind(listener: ScoreItemClickListener, score: Score, position: Int) {
            _binding.score = score
            _binding.scoreIndex = position
            _binding.clickListener = listener
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            _binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentScoreItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class ScoreItemClickListener(val clickListener: (score: Score, position: Int) -> Unit) {
    fun onClick(score: Score, position: Int) = clickListener(score, position)
}
