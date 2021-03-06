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
import com.game.score.models.xml.receive.CompetitorInfoAll

/**
 * [RecyclerView.Adapter] that can display a [CompetitorInfo.CompetitorInfoClass.Score].
 */
class ScoreListAdapter(
    private val _viewModel: MainViewModel,
    private val _clickListener: ScoreItemClickListener
) : ListAdapter<CompetitorInfoAll.CompetitorInfoClass.ScoreClass, ScoreListAdapter.ViewHolder>(
    DiffCallback
) {
    companion object DiffCallback :
        DiffUtil.ItemCallback<CompetitorInfoAll.CompetitorInfoClass.ScoreClass>() {
        override fun areItemsTheSame(
            oldItem: CompetitorInfoAll.CompetitorInfoClass.ScoreClass,
            newItem: CompetitorInfoAll.CompetitorInfoClass.ScoreClass
        ): Boolean {
            return oldItem.ScoreID == newItem.ScoreID
        }

        override fun areContentsTheSame(
            oldItem: CompetitorInfoAll.CompetitorInfoClass.ScoreClass,
            newItem: CompetitorInfoAll.CompetitorInfoClass.ScoreClass
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
            score: CompetitorInfoAll.CompetitorInfoClass.ScoreClass,
            position: Int
        ) {
            with(_binding) {
                this.score = score
                scoreIndex = position
                clickListener = listener
                if (!score.ScoreErrorMessage.isBlank() && score.ScoreStatus == ScoreConsts.ScoreStatus_Error)
                    itemScoreScoreValue.error = score.ScoreErrorMessage
                else itemScoreScoreValue.error = null

                //region 设置颜色
                //region itemScoreScoreName
                if (score.ScoreValue.isBlank()) //无分数
                    itemScoreScoreName.setTextColor(
                        ContextCompat.getColor(root.context, R.color.colorScoreName_NoScore)
                    )
                else //有分数
                    itemScoreScoreName.setTextColor(
                        ContextCompat.getColor(
                            root.context,
                            R.color.colorScoreName_HasScore
                        )
                    )
                //endregion

                //region itemScoreScoreValue
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
                //endregion
                //endregion

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

class ScoreItemClickListener(val clickListener: (score: CompetitorInfoAll.CompetitorInfoClass.ScoreClass, position: Int) -> Unit) {
    fun onClick(score: CompetitorInfoAll.CompetitorInfoClass.ScoreClass, position: Int) {
        ExceptionHandlerUtil.usingExceptionHandler {
            clickListener(score, position)
        }
    }
}
