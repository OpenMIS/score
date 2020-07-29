package com.game.score.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
        _binding = FragmentScoreListBinding.inflate(inflater, container, false)

        //region layout里的数据与数据模型关联
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        _binding.lifecycleOwner = this

        //使用MainActivity创建的MainViewModel实例
        _viewModel = ViewModelProvider(activity as FragmentActivity)
            .get<MainViewModel>(MainViewModel::class.java)
        Log.d("1111", "onCreateView: " + _viewModel.hashCode())
        _binding.viewModel = _viewModel
        //endregion

        // region 设置适配器
        _adapter = ScoreListAdapter(ScoreItemClickListener {

        })

        with(_binding.list) {
            layoutManager = LinearLayoutManager(context)
            adapter = _adapter
        }
        // endregion

        return _binding.root
    }
}