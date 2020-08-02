package com.game.score.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.game.score.core.ExceptionHandlerUtil
import com.game.score.databinding.FragmentScoreListBinding


/**
 * A fragment representing a list of Items.
 */
class ScoreListFragment : Fragment() {

    //region 字段
    /**
     * 视图模型
     */
    private lateinit var _viewModel: MainViewModel

    /**
     * 数据绑定
     */
    private lateinit var _binding: FragmentScoreListBinding

    private lateinit var _adapter: ScoreListAdapter
    //endregion

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ExceptionHandlerUtil.usingExceptionHandler {
            _binding = FragmentScoreListBinding.inflate(inflater, container, false)

            //region layout里的数据与数据模型关联
            // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
            _binding.lifecycleOwner = this

            //使用MainActivity创建的MainViewModel实例
            _viewModel = ViewModelProvider(activity as FragmentActivity)
                .get<MainViewModel>(MainViewModel::class.java)
            _binding.viewModel = _viewModel
            //endregion

            // region 设置适配器
            _adapter = ScoreListAdapter(_viewModel, ScoreItemClickListener { score, position ->
                if (!score.getOrder().isBlank()) {
                    _viewModel.currentScore.value = score
                    _viewModel.currentScoreIndex.value = position

                    //【注意】此处需要通知刷新全部，这样选择行的样式才有效果。
                    _adapter.notifyDataSetChanged()
                }
            })

            with(_binding.scoreList) {
                val linearLayoutManager = LinearLayoutManager(context)

                layoutManager = linearLayoutManager
                adapter = _adapter

                //加入分隔线
                addItemDecoration(
                    DividerItemDecoration(context, linearLayoutManager.orientation)
                )
            }
            // endregion
        }
        
        return _binding.root
    }
}