package com.game.score.ui.main

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.game.score.R
import com.game.score.core.ExceptionHandlerUtil
import com.game.score.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    //region 字段
    /**
     * 视图模型
     */
    private lateinit var _viewModel: MainViewModel

    /**
     * 数据绑定
     */
    private lateinit var _binding: FragmentMainBinding

    //region 数字按钮以及.与x按钮事件 字段
    /**
     * 数字按钮以及.与x按钮事件 字段
     */
    private lateinit var _buttonListener: View.OnClickListener
    //endregion
    //endregion

    //region 复写方法
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ExceptionHandlerUtil.usingExceptionHandler {
            _binding = FragmentMainBinding.inflate(inflater, container, false)

            //region layout里的数据与数据模型关联
            // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
            _binding.lifecycleOwner = this
            //使用MainActivity创建的MainViewModel实例
            _viewModel = ViewModelProvider(activity as FragmentActivity)
                .get(MainViewModel::class.java)
            //endregion

            _buttonListener = ButtonOnClickListener(this, _viewModel)
            with(_binding) {
                viewModel = _viewModel

                val recyclerView = root.findViewById<RecyclerView>(R.id.score_list)
                val scoreListAdapter = recyclerView.adapter as ScoreListAdapter

                _viewModel.scoreListChangeListener = {
                    scoreListAdapter.notifyDataSetChanged()
                }

                //region 挂接按钮事件
                for (button in listOf(
                    button0, button1, button2, button3,
                    button4, button5, button6, button7,
                    button8, button9, buttonDot,
                    imageButtonX, buttonV, buttonSend, buttonP, buttonN
                ))
                    button.setOnClickListener(_buttonListener)
                //endregion

                //region 长按“设备代码”打开“设置”页面
                textViewJudgeName.setOnLongClickListener {
                    val controller = Navigation.findNavController(it)
                    controller.navigate(R.id.settingsFragment)

                    return@setOnLongClickListener true
                }
                //endregion

                if (lifecycleOwner != null)
                    _viewModel.competitorName_Normal.observe(lifecycleOwner!!, {
                        if (it) {//普通显示 情况
                            textViewCompetitorName.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                0,
                                0
                            )

                            textViewCompetitorName.setTextSize(
                                TypedValue.COMPLEX_UNIT_PX,
                                resources.getDimension(R.dimen.textView_textSize)
                            )
                        } else {//显示成绩确认成功的提示消息 情况
                            textViewCompetitorName.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_baseline_sentiment_satisfied_alt_24,
                                0,
                                0,
                                0
                            )
                            textViewCompetitorName.setTextSize(
                                TypedValue.COMPLEX_UNIT_PX,
                                resources.getDimension(R.dimen.textView_validate_success_textSize)
                            )
                        }
                    })
            }
        }

        return _binding.root
    }
//endregion
}