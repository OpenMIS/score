package com.game.score.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.game.score.ScoreModel
import com.game.score.databinding.FragmentScoreItemBinding

/**
 * [RecyclerView.Adapter] that can display a [ScoreModel].
 */
class ScoreListAdapter(
    private val _clickListener: ScoreItemClickListener
) : ListAdapter<ScoreModel, ScoreListAdapter.ViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<ScoreModel>() {
        override fun areItemsTheSame(oldItem: ScoreModel, newItem: ScoreModel): Boolean {
            return oldItem.order == newItem.order
        }

        override fun areContentsTheSame(oldItem: ScoreModel, newItem: ScoreModel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(_clickListener, getItem(position))

    class ViewHolder(private var _binding: FragmentScoreItemBinding) :
        RecyclerView.ViewHolder(_binding.root) {
        fun bind(listener: ScoreItemClickListener, scoreModel: ScoreModel) {
            _binding.scoreModel = scoreModel
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

class ScoreItemClickListener(val clickListener: (scoreModel: ScoreModel) -> Unit) {
    fun onClick(scoreModel: ScoreModel) = clickListener(scoreModel)
}
