package com.game.score.ui.main

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
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

    //region 数字按钮以及.与x按钮事件 字段
    /**
     * 数字按钮以及.与x按钮事件 字段
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
                    R.id.button_send -> send(recyclerView)
                    R.id.button_V -> validate(it)
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
    //endregion
    //endregion

    //region 工具集
    //region 发送ScoreList分数列表给服务端
    /**
     * 发送ScoreList分数列表给服务端
     */
    private fun sendScoreList() {
        if (_viewModel.competitorInfo.value != null &&
            !_viewModel.competitorInfo.value!!.CompetitorInfo.Score.isNullOrEmpty()
        )
            with(_viewModel.competitorInfo.value!!.CompetitorInfo) {
                val scores =
                    mutableListOf<ScoreList.ScoreListClass.ScoreClass>()
                Score?.forEach {
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
    }
    //endregion

    //region 发送按钮(S)内部调用此方法
    /**
     * 发送按钮(S)内部调用此方法
     */
    private fun send(
        recyclerView: RecyclerView
    ) {
        sendScoreList() //发送ScoreList分数列表给服务端
        //region 载入并定位到下一条记录
        if (_viewModel.competitorInfo.value?.CompetitorInfo?.Score != null &&
            _viewModel.currentScoreIndex.value != null &&
            _viewModel.currentScoreIndex.value!! <
            _viewModel.competitorInfo.value?.CompetitorInfo?.Score!!.count() - 1
        ) {
            val nextIndex = _viewModel.currentScoreIndex.value!! + 1
            val nextScore =
                _viewModel.competitorInfo.value?.CompetitorInfo?.Score?.get(
                    nextIndex
                )
            if (nextScore != null && nextScore.isScoring()) {
                _viewModel.currentScoreIndex.value = nextIndex
                _viewModel.currentScore.value =
                    _viewModel.competitorInfo.value?.CompetitorInfo?.Score?.get(
                        nextIndex
                    )
   
                //定位到指定项如果该项可以置顶就将其置顶显示。比如:微信联系人的字母索引定位就是采用这种方式实现。
                (recyclerView.layoutManager as LinearLayoutManager?)!!.scrollToPositionWithOffset(
                    nextIndex,
                    /*距离顶部的像素。通过此值，让正在打分的项尽量列表的上下的中间位置，
                    这样方便看到之前打分与之后要打的分。
                    */
                    100
                )

                /*smoothScrollToPosition(position)和scrollToPosition(position)效果基本相似，
                也是把你想显示的项显示出来，只要那一项现在看得到了，那它就罢工了，
                不同的是smoothScrollToPosition是平滑到你想显示的项，而scrollToPosition是直接定位显示！*/
                //recyclerView.smoothScrollToPosition(nextIndex)
            }
            //endregion
        }
    }
    //endregion

    //region 确认成绩(V)按钮内部调用此方法
    /**
     * 确认成绩(V)按钮内部调用此方法
     */
    private fun validate(button: View) {

        val emptyScoreValueCount = _viewModel.competitorInfo.value?.CompetitorInfo?.Score?.count {
            !arrayOf(
                "F_0",
                "F_Status",
                "F_TotalScore"
            ).contains(it.ScoreID) && it.ScoreValue.isBlank()
        }

        val emptyScoreValueCountString =
            if (emptyScoreValueCount != null && emptyScoreValueCount > 0)
                getString(
                    R.string.alertDialog_message_confirm_NoScoreValueCount,
                    emptyScoreValueCount
                )
            else ""

        val message = emptyScoreValueCountString + getString(
            R.string.alertDialog_message_confirm
        )

        val builder =
            AlertDialog.Builder(button.context)
                .setTitle(R.string.alertDialog_title_confirm)
                .setMessage(message)
                .setPositiveButton(R.string.button_text_no, null) //监听下方button点击事件
                .setNegativeButton(R.string.button_text_yes) { _, _ ->
                    val validateRow = _viewModel.competitorInfo.value?.CompetitorInfo?.Score?.find {
                        it.ScoreID == "F_Status"
                    }
                    if (validateRow != null) {
                        validateRow.ScoreValue = "1" //表示确认成绩
                        sendScoreList() //发送ScoreList分数列表给服务端
                    }
                }.setCancelable(true) //设置对话框是可取消的

        val dialog = builder.create()
        dialog.show()
    }
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
            with(_binding) {
                //使用MainActivity创建的MainViewModel实例
                _viewModel = ViewModelProvider(activity as FragmentActivity)
                    .get<MainViewModel>(MainViewModel::class.java)
                viewModel = _viewModel
                //endregion

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
                    imageButtonX, buttonV, buttonSend
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
                //val a =
                if (lifecycleOwner != null)
                    _viewModel.eventAndPhase_Normal.observe(lifecycleOwner!!, Observer<Boolean> {
                        if (it) {//普通显示 情况
                            textViewEventAndPhase.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                0,
                                0
                            )

                            textViewEventAndPhase.setTextSize(
                                TypedValue.COMPLEX_UNIT_PX,
                                resources.getDimension(R.dimen.textView_textSize)
                            )
                        } else {//显示成绩确认成功的提示消息 情况
                            textViewEventAndPhase.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_baseline_sentiment_satisfied_alt_24,
                                0,
                                0,
                                0
                            )
                            textViewEventAndPhase.setTextSize(
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