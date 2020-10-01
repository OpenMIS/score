package com.game.score.ui.main

import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.game.score.Controller
import com.game.score.MainActivity
import com.game.score.R
import com.game.score.ScoreConsts
import com.game.score.core.*
import com.game.score.models.StepRange
import com.game.score.models.xml.send.ScoreList

/**
 * 主界面上的按钮点击倾听器
 */
class ButtonOnClickListener(
    private val mainFragment: MainFragment, private val mainViewModel: MainViewModel
) : View.OnClickListener {
    //region 工具集
    //region 发送ScoreList分数列表给服务端
    /**
     * 发送ScoreList分数列表给服务端
     */
    private fun sendScoreList(): Boolean {
        var result = false
        if (mainViewModel.currentCompetitorInfo.value != null &&
            !mainViewModel.currentCompetitorInfo.value!!.Score.isNullOrEmpty()
        )

            if (mainViewModel.currentScore.value != null &&
                !mainViewModel.currentScore.value!!.ScoreValue.isBlank() &&
                !mainViewModel.currentScore.value!!.isVaild()
            ) {
                mainViewModel.currentScore.value!!.ScoreErrorMessage =
                    ScoreConsts.ScoreErrorMessage_Vaild

                mainViewModel.currentScore.value!!.ScoreStatus = ScoreConsts.ScoreStatus_Error
            } else {
                with(mainViewModel.currentCompetitorInfo.value!!) {
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


                    val message = ScoreList(
                        ScoreList = ScoreList.ScoreListClass(
                            CompetitorID = CompetitorID,
                            Score = scores
                        )
                    )

                    //region 盛装舞步配对赛
                    val isDRPairMatch =
                        mainViewModel.competitorInfoAll.value?.IsDRPairMatch ?: false //是否装舞步配对赛
                    val stepRange: StepRange?
                    if (isDRPairMatch) {
                        stepRange = ScoreUtil.drJudgeSelectStepRange(
                            mainViewModel.competitorInfoAll.value,
                            mainViewModel.currentCompetitorInfo.value
                        )

                        if (stepRange != null)
                            message.ScoreList.apply {
                                ScoreStart = stepRange.start
                                ScoreEnd = stepRange.end
                            }
                    }
                    //endregion

                    message.sendInUI()

                    result = true
                }
            }

        return result
    }
    //endregion

    //region 发送按钮(S)内部调用此方法
    /**
     * 发送按钮(S)内部调用此方法
     */
    private fun send(
        recyclerView: RecyclerView
    ) {
        if (sendScoreList()) //发送ScoreList分数列表给服务端
        {
            //region 载入并定位到下一条记录
            if (mainViewModel.currentCompetitorInfo.value?.Score != null &&
                mainViewModel.currentScoreIndex.value != null &&
                mainViewModel.currentScoreIndex.value!! <
                mainViewModel.currentCompetitorInfo.value?.Score!!.count() - 1
            ) {
                val nextIndex = mainViewModel.currentScoreIndex.value!! + 1
                val nextScore =
                    mainViewModel.currentCompetitorInfo.value?.Score?.get(
                        nextIndex
                    )
                if (nextScore != null && nextScore.isScoring()) {
                    mainViewModel.currentScoreIndex.value = nextIndex
                    mainViewModel.currentScore.value =
                        mainViewModel.currentCompetitorInfo.value?.Score?.get(
                            nextIndex
                        )

                    Controller.scrollToScoreIndex(nextIndex, recyclerView)

                    /*smoothScrollToPosition(position)和scrollToPosition(position)效果基本相似，
                    也是把你想显示的项显示出来，只要那一项现在看得到了，那它就罢工了，
                    不同的是smoothScrollToPosition是平滑到你想显示的项，而scrollToPosition是直接定位显示！*/
                    //recyclerView.smoothScrollToPosition(nextIndex)
                }
                //endregion
            }
            //endregion
        }
    }
    //endregion

    //region 确认成绩(V)按钮内部调用此方法
    /**
     * 确认成绩(V)按钮内部调用此方法
     */
    private fun validate(button: View, recyclerView: RecyclerView) {
        if (!Controller.isWifiNetworkAvailable(mainFragment.requireContext()))
            return //无Wifi直接退出本方法

        val remainMustScoredCount =
            mainViewModel.currentCompetitorInfo.value?.remainMustScoredCount()
        val emptyScoreValueCountString =
            if (remainMustScoredCount != null && remainMustScoredCount > 0)
                mainFragment.getString(
                    R.string.alertDialog_message_confirm_NoScoreValueCount,
                    remainMustScoredCount
                )
            else ""

        val message = emptyScoreValueCountString + mainFragment.getString(
            R.string.alertDialog_message_confirmResult
        )

        val builder =
            AlertDialog.Builder(button.context)
                .setTitle(R.string.alertDialog_title_confirmResult)
                .setMessage(message)
                .setPositiveButton(R.string.button_text_no) { _, _ ->
                    ExceptionHandlerUtil.usingExceptionHandler {
                        //定位到第一条分数为空的记录上，并设置此记录为当前记录。
                        Controller.goToFirstEmptyScoreAndSetCurrent(mainViewModel, recyclerView)
                    }
                }
                //监听下方button点击事件
                .setNegativeButton(R.string.button_text_yes) { _, _ ->
                    ExceptionHandlerUtil.usingExceptionHandler {
                        val validateRow =
                            mainViewModel.currentCompetitorInfo.value?.Score?.find {
                                it.ScoreID == ScoreConsts.Attribute_F_Status
                            }
                        if (validateRow != null) {
                            validateRow.ScoreValue = ScoreConsts.Status_ScoreValue_Validate //表示确认成绩
                            sendScoreList() //发送ScoreList分数列表给服务端
                        }
                    }
                }.setCancelable(true) //设置对话框是可取消的

        val dialog = builder.create()
        dialog.show()
    }
    //endregion

    //region “下一人”按钮内部调用此方法
    /**
     * “下一人”按钮内部调用此方法
     */
    private fun next(recyclerView: RecyclerView) {
        Controller.next(mainViewModel, mainFragment.activity as MainActivity, recyclerView)
    }
    //endregion

    //region “上一人”按钮内部调用此方法
    /**
     * “上一人”按钮内部调用此方法
     */
    private fun previous(recyclerView: RecyclerView) {
        with(mainViewModel.currentCompetitorInfoIndex) {
            if (mainViewModel.competitorInfoAll.value != null && value != null && value!! >= 0) {
                val count = mainViewModel.competitorInfoAll.value!!.CompetitorInfo!!.count()
                value = if (value!! == 0)
                    count - 1
                else value!! - 1

                if (value!! == count - 1) {
                    Toast.makeText(
                        mainFragment.activity,
                        mainFragment.getString(R.string.toast_end),
                        Toast.LENGTH_SHORT
                    ).setPosition().show()
                } else if (value!! == 0) {
                    Toast.makeText(
                        mainFragment.activity,
                        mainFragment.getString(R.string.toast_first),
                        Toast.LENGTH_SHORT
                    ).setPosition().show()
                }

                mainViewModel.currentCompetitorInfo.value =
                    mainViewModel.competitorInfoAll.value!!.CompetitorInfo!![value!!]
                Controller.updateMainViewModel(mainViewModel, mainFragment.activity as MainActivity)
                Controller.goToFirstEmptyScoreAndSetCurrent(mainViewModel, recyclerView)
            }
        }
    }
    //endregion
    //endregion

    override fun onClick(view: View?) {
        if (view == null || mainViewModel.currentScore.value == null ||
            mainViewModel.currentScoreIndex.value == null ||
            mainViewModel.currentScoreIndex.value!! < 0
        ) //如果没有当前的分数模型，直接退出本方法。
            return

        ExceptionHandlerUtil.usingExceptionHandler {
            //先异步保存到磁盘
            CompetitorInfoAllManager.saveAsync(mainViewModel.competitorInfoAll.value)

            val recyclerView = view.rootView.findViewById<RecyclerView>(R.id.score_list)
            val scoreListAdapter = recyclerView.adapter as ScoreListAdapter

            var numberString = String()
            when (view.id) {
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

            val scoreValue = mainViewModel.currentScore.value?.ScoreValue
            val scoreValueIsNullOrBlank = scoreValue.isNullOrBlank()
            if (!numberString.isBlank()) {
                if (scoreValueIsNullOrBlank ||
                    scoreValue.toString().length <= 5
                ) {
                    if (scoreValueIsNullOrBlank || view.id != R.id.button_dot ||
                        //之前的字符串没有包含.字符
                        !scoreValue!!.contains('.')
                    )
                        mainViewModel.currentScore.value!!.ScoreValue += numberString
                } else
                    mainViewModel.currentScore.value!!.ScoreValue = numberString
            } else {
                when (view.id) {
                    R.id.imageButton_X -> {
                        if (!scoreValue.isNullOrEmpty()) {
                            mainViewModel.currentScore.value!!.ScoreValue = scoreValue.substring(
                                0, scoreValue.length - 1
                            )
                        }
                    }
                    R.id.button_send -> send(recyclerView)
                    R.id.button_V -> validate(view, recyclerView)
                    R.id.button_P -> previous(recyclerView)
                    R.id.button_N -> next(recyclerView)
                }
            }

            if (mainViewModel.currentScore.value?.ScoreValue == ".")
                mainViewModel.currentScore.value!!.ScoreValue = "0."

            //region 触发界面更新
            mainViewModel.currentScore.postValue(mainViewModel.currentScore.value)

            //【注意】此处需要通知刷新全部，这样选择行的样式才有效果。
            //scoreListAdapter.notifyItemChanged(mainViewModel.currentScoreIndex.value!!)
            scoreListAdapter.notifyDataSetChanged()
            //endregion
        }
    }
}