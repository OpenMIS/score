package com.game.score.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.game.score.R
import com.game.score.ScoreConsts
import com.game.score.core.ExceptionHandlerUtil
import com.game.score.databinding.FragmentScoreItemBinding
import com.game.score.models.xml.receive.CompetitorInfo

/**
 * [RecyclerView.Adapter] that can display a [CompetitorInfo.CompetitorInfoClass.Score].
 */
class ScoreListAdapter(
    private val _viewModel: MainViewModel,
    private val _clickListener: ScoreItemClickListener
) : ListAdapter<CompetitorInfo.CompetitorInfoClass.ScoreClass, ScoreListAdapter.ViewHolder>(
    DiffCallback
) {
    companion object DiffCallback :
        DiffUtil.ItemCallback<CompetitorInfo.CompetitorInfoClass.ScoreClass>() {
        override fun areItemsTheSame(
            oldItem: CompetitorInfo.CompetitorInfoClass.ScoreClass,
            newItem: CompetitorInfo.CompetitorInfoClass.ScoreClass
        ): Boolean {
            return oldItem.ScoreID == newItem.ScoreID
        }

        override fun areContentsTheSame(
            oldItem: CompetitorInfo.CompetitorInfoClass.ScoreClass,
            newItem: CompetitorInfo.CompetitorInfoClass.ScoreClass
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        ExceptionHandlerUtil.usingExceptionHandler {
            holder.bind(_viewModel, _clickListener, getItem(position), position)
        }
    }

    class ViewHolder(private var _binding: FragmentScoreItemBinding) :
        RecyclerView.ViewHolder(_binding.root) {

        fun bind(
            viewModel: MainViewModel,
            listener: ScoreItemClickListener,
            score: CompetitorInfo.CompetitorInfoClass.ScoreClass,
            position: Int
        ) {
            with(_binding) {
                this.score = score
                scoreIndex = position
                clickListener = listener
                if (!score.ScoreErrorMessage.isBlank() && score.ScoreStatus == ScoreConsts.ScoreStatus_Error)
                    itemScoreScoreValue.error = score.ScoreErrorMessage
                else itemScoreScoreValue.error = null

                if (score.ScoreStatus == ScoreConsts.ScoreStatus_Done)
                    itemScoreScoreValue.setTextColor(
                        ContextCompat.getColor(root.context, R.color.colorScoreValue_Done)
                    )
                else itemScoreScoreValue.setTextColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.colorScoreValue_NonDone
                    )
                )

                root.isSelected = viewModel.currentScoreIndex.value == position
                // This is important, because it forces the data binding to execute immediately,
                // which allows the RecyclerView to make the correct view size measurements
                executePendingBindings()
            }
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

class ScoreItemClickListener(val clickListener: (score: CompetitorInfo.CompetitorInfoClass.ScoreClass, position: Int) -> Unit) {
    fun onClick(score: CompetitorInfo.CompetitorInfoClass.ScoreClass, position: Int) {
        ExceptionHandlerUtil.usingExceptionHandler {
            clickListener(score, position)
        }
    }
}
