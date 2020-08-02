package com.game.score.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.game.score.R
import com.game.score.core.ExceptionHandlerUtil
import com.game.score.core.sendInUI
import com.game.score.databinding.FragmentMainBinding
import com.game.score.models.xml.send.ScoreList

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
    //endregion

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
                .get<MainViewModel>(MainViewModel::class.java)
            _binding.viewModel = _viewModel
            //endregion

            val recyclerView = _binding.root.findViewById<RecyclerView>(R.id.score_list)
            val scoreListAdapter = recyclerView.adapter as ScoreListAdapter

            _viewModel.scoreListChangeListener = {
                scoreListAdapter.notifyDataSetChanged()
            }

            //region 挂接按钮事件
            for (button in listOf(
                _binding.button0, _binding.button1, _binding.button2, _binding.button3,
                _binding.button4, _binding.button5, _binding.button6, _binding.button7,
                _binding.button8, _binding.button9, _binding.buttonDot,
                _binding.imageButtonX, _binding.buttonV, _binding.buttonSend
            ))
                button.setOnClickListener(_buttonListener)
            //endregion

            //region 长按“设备代码”打开“设置”页面
            _binding.textViewJudgeName.setOnLongClickListener {
                val controller = Navigation.findNavController(it)
                controller.navigate(R.id.settingsFragment)

                return@setOnLongClickListener true
            }
            //endregion
        }

        return _binding.root
    }

    /**
     * 按钮事件
     */
    private val _buttonListener = View.OnClickListener {
        if (_viewModel.currentScore.value == null ||
            _viewModel.currentScoreIndex.value == null ||
            _viewModel.currentScoreIndex.value!! < 0
        ) //如果没有当前的分数模型，直接退出本方法。
            return@OnClickListener

        ExceptionHandlerUtil.usingExceptionHandler {
            val recyclerView = it.rootView.findViewById<RecyclerView>(R.id.score_list)
            val scoreListAdapter = recyclerView.adapter as ScoreListAdapter

            var numberString = String()
            when (it.id) {
                R.id.button0 -> numberString = "0"
                R.id.button1 -> numberString = "1"
                R.id.button2 -> numberString = "2"
                R.id.button3 -> numberString = "3"
                R.id.button4 -> numberString = "4"
                R.id.button5 -> numberString = "5"
                R.id.button6 -> numberString = "6"
                R.id.button7 -> numberString = "7"
                R.id.button8 -> numberString = "8"
                R.id.button9 -> numberString = "9"
                R.id.button_dot -> numberString = "."
            }

            val scoreValue = _viewModel.currentScore.value?.ScoreValue
            val scoreValueIsNullOrBlank = scoreValue.isNullOrBlank()
            if (!numberString.isBlank()) {
                if (scoreValueIsNullOrBlank ||
                    scoreValue.toString().length <= 5
                ) {
                    if (scoreValueIsNullOrBlank || it.id != R.id.button_dot ||
                        //之前的字符串没有包含.字符
                        !scoreValue!!.contains('.')
                    )
                        _viewModel.currentScore.value!!.ScoreValue += numberString
                } else
                    _viewModel.currentScore.value!!.ScoreValue = numberString
            } else {
                when (it.id) {
                    R.id.imageButton_X -> {
                        if (!scoreValue.isNullOrEmpty()) {
                            _viewModel.currentScore.value!!.ScoreValue = scoreValue.substring(
                                0, scoreValue.length - 1
                            )
                        }
                    }
                    R.id.button_send -> {
                        //region 载入并定位到下一条记录
                        if (_viewModel.competitorInfo.value?.CompetitorInfo?.Scores != null &&
                            _viewModel.currentScoreIndex.value != null &&
                            _viewModel.currentScoreIndex.value!! <
                            _viewModel.competitorInfo.value?.CompetitorInfo?.Scores!!.count() - 1
                        ) {
                            //region 把分数列表发送给服务端。按键S
                            if (_viewModel.competitorInfo.value != null &&
                                !_viewModel.competitorInfo.value!!.CompetitorInfo.Scores.isNullOrEmpty()
                            )
                                with(_viewModel.competitorInfo.value!!.CompetitorInfo) {
                                    val scores =
                                        mutableListOf<ScoreList.ScoreListClass.ScoreClass>()
                                    Scores?.forEach {
                                        scores.add(
                                            ScoreList.ScoreListClass.ScoreClass(
                                                it.ScoreID,
                                                it.ScoreValue
                                            )
                                        )
                                    }

                                    ScoreList(
                                        ScoreList = ScoreList.ScoreListClass(
                                            CompetitorID = CompetitorID,
                                            Score = scores
                                        )
                                    ).sendInUI()
                                }
                            //endregion

                            val nextIndex = _viewModel.currentScoreIndex.value!! + 1
                            val nextScore =
                                _viewModel.competitorInfo.value?.CompetitorInfo?.Scores?.get(
                                    nextIndex
                                )
                            if (nextScore != null && nextScore.isScoring()) {
                                _viewModel.currentScoreIndex.value = nextIndex
                                _viewModel.currentScore.value =
                                    _viewModel.competitorInfo.value?.CompetitorInfo?.Scores?.get(
                                        nextIndex
                                    )

                                //定位到指定项如果该项可以置顶就将其置顶显示。比如:微信联系人的字母索引定位就是采用这种方式实现。
                                (recyclerView.layoutManager as LinearLayoutManager?)!!.scrollToPositionWithOffset(
                                    nextIndex,
                                    0
                                )

                                /*smoothScrollToPosition(position)和scrollToPosition(position)效果基本相似，
                                也是把你想显示的项显示出来，只要那一项现在看得到了，那它就罢工了，
                                不同的是smoothScrollToPosition是平滑到你想显示的项，而scrollToPosition是直接定位显示！*/
                                //recyclerView.smoothScrollToPosition(nextIndex)
                            }
                        }
                        //endregion
                    }
                    R.id.button_V -> {
                        val builder =
                            AlertDialog.Builder(it.context)
                                .setTitle(R.string.alertDialog_title_confirm)
                                .setMessage(R.string.alertDialog_message_confirm)
                                .setPositiveButton(R.string.button_text_no, null) //监听下方button点击事件
                                .setNegativeButton(R.string.button_text_yes) { _, _ ->

                                    Toast.makeText(it.context, "确认", Toast.LENGTH_SHORT).show()

                                }.setCancelable(true) //设置对话框是可取消的

                        val dialog = builder.create()
                        dialog.show()
                    }
                }
            }

            if (_viewModel.currentScore.value?.ScoreValue == ".")
                _viewModel.currentScore.value!!.ScoreValue = "0."

            //region 触发界面更新
            _viewModel.currentScore.postValue(_viewModel.currentScore.value)

            //【注意】此处需要通知刷新全部，这样选择行的样式才有效果。
            //scoreListAdapter.notifyItemChanged(_viewModel.currentScoreIndex.value!!)
            scoreListAdapter.notifyDataSetChanged()
            //endregion
        }
    }
}