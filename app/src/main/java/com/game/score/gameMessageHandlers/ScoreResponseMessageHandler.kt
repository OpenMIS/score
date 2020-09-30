package com.game.score.gameMessageHandlers

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.game.score.Controller
import com.game.score.MainActivity
import com.game.score.R
import com.game.score.ScoreConsts
import com.game.score.core.*
import com.game.score.models.xml.receive.ScoreResponse
import com.game.score.ui.main.MainViewModel

object ScoreResponseMessageHandler : IGameMessageHandlerEx {
    //region 处理消息
    /**
     * 处理消息
     */
    override fun handle(
        messageModel: IGameMessageModel,
        mainViewModel: MainViewModel,
        mainActivity: MainActivity
    ) {
        if (messageModel is ScoreResponse) {
            with(mainViewModel) {
                //region ScoreResponse消息处理
                if (currentCompetitorInfo.value != null &&
                    currentCompetitorInfo.value!!.Score != null && //表示需要打分

                    //同一个运动员 或者 同一组（多人项目）
                    currentCompetitorInfo.value!!.CompetitorID ==
                    messageModel.CompetitorInfo.CompetitorID
                ) {
                    var change = false
                    messageModel.CompetitorInfo.Score?.forEach { score ->
                        currentCompetitorInfo.value!!.Score!!.find {
                            it.ScoreID == score.ScoreID
                        }.let { findResult ->
                            if (findResult != null) {
                                if (findResult.ScoreStatus != score.ScoreStatus) {
                                    findResult.ScoreStatus = score.ScoreStatus
                                    change = true
                                }

                                if (findResult.ScoreErrorMessage != score.ScoreErrorMessage) {
                                    findResult.ScoreErrorMessage = score.ScoreErrorMessage
                                    change = true
                                }

                                //【注意】由于服务端回应的分数可能为空的，所以需要判断一下。
                                if (!score.ScoreValue.isBlank() && findResult.ScoreValue != score.ScoreValue) {
                                    findResult.ScoreValue = score.ScoreValue
                                    change = true
                                }
                            }
                        }
                    }

                    if (change && scoreListChangeListener != null) {
                        scoreListChangeListener!!.invoke(this)
                    }

                    val firstErrorScore = messageModel.CompetitorInfo.Score?.find {
                        it.ScoreStatus == ScoreConsts.ScoreStatus_Error
                    }

                    //region 分数有错误时，定位到错误的记录上。
                    if (firstErrorScore != null) { //说明分数有错误
                        //【注意】indexOfFirst如果找不到对应的，会返回-1。
                        val errorIndex =
                            currentCompetitorInfo.value?.Score?.indexOfFirst {
                                it.ScoreID == firstErrorScore.ScoreID
                            }

                        if (errorIndex != null && errorIndex >= 0) { //找到错误的分数
                            currentScoreIndex.value = errorIndex
                            currentScore.value =
                                currentCompetitorInfo.value!!.Score!![errorIndex]

                            //region 定位到第一条错误的分数上
                            val recyclerView =
                                mainActivity.findViewById<RecyclerView>(R.id.score_list)
                            Controller.scrollToScoreIndex(errorIndex, recyclerView)
                            //endregion

                            recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }
                    //endregion

                    //region 确认成绩的回应
                    val validateRowInApp = currentCompetitorInfo.value?.Score?.find {
                        it.ScoreID == ScoreConsts.Attribute_F_Status && it.ScoreValue == ScoreConsts.Status_ScoreValue_Validate
                    }

                    if (validateRowInApp != null) {
                        val remainMustScoredCount =
                            currentCompetitorInfo.value?.remainMustScoredCount()
                        if (messageModel.CompetitorInfo.Score == null || //没有分数列表
                            firstErrorScore == null && remainMustScoredCount ?: 0 == 0 //没有错误并且已经没有剩余打分项
                        ) {//服务端确认成绩成功
                            val findResult =
                                mainViewModel.competitorInfoAll.value?.CompetitorInfo?.find {
                                    it.CompetitorID == messageModel.CompetitorInfo.CompetitorID
                                }

                            if (findResult != null) {
                                mainViewModel.competitorInfoAll.value?.CompetitorInfo?.remove(
                                    findResult
                                )

                                CompetitorInfoAllManager.saveAsync(mainViewModel.competitorInfoAll.value)
                            }

                            if (!Controller.next(
                                    mainViewModel,
                                    mainActivity,
                                    forDeleteCompetitorInfo = true
                                )
                            )
                                Controller.haveABreak(
                                    mainViewModel,
                                    mainActivity, true
                                ) //休息一下

                            Toast.makeText(
                                mainActivity,
                                R.string.validate_success,
                                Toast.LENGTH_SHORT
                            ).setPosition().show()
                        } else {//服务端确认成绩失败
                            Toast.makeText(
                                mainActivity,
                                R.string.validate_Fail,
                                Toast.LENGTH_LONG
                            ).setPosition().show()

                            if (firstErrorScore != null)  //说明分数有错误
                                validateRowInApp.ScoreValue = "" //清空确认，表示未确认成绩。
                            else {
                                //region 提示是否强制跳过打分
                                val emptyScoreValueCountString =
                                    if (remainMustScoredCount != null && remainMustScoredCount > 0)
                                        mainActivity.getString(
                                            R.string.alertDialog_message_confirm_NoScoreValueCount,
                                            remainMustScoredCount
                                        )
                                    else ""

                                val message =
                                    emptyScoreValueCountString + mainActivity.getString(
                                        R.string.alertDialog_message_ForceSkipScoring
                                    )
                                val recyclerView =
                                    mainActivity.findViewById<RecyclerView>(R.id.score_list)

                                val builder =
                                    AlertDialog.Builder(mainActivity)
                                        .setTitle(R.string.alertDialog_title_confirm)
                                        .setMessage(message)
                                        //监听左边（负）button点击事件
                                        .setNegativeButton(R.string.button_text_yes) { _, _ ->
                                            ExceptionHandlerUtil.usingExceptionHandler {
                                                //region 再次确认
                                                val builder2 =
                                                    AlertDialog.Builder(mainActivity)
                                                        .setTitle(R.string.alertDialog_title_confirmAgain)
                                                        .setMessage(message)
                                                        //监听左边（负）button点击事件
                                                        .setNegativeButton(R.string.button_text_yes) { _, _ ->
                                                            ExceptionHandlerUtil.usingExceptionHandler {
                                                                val findResult =
                                                                    mainViewModel.competitorInfoAll.value?.CompetitorInfo?.find {
                                                                        it.CompetitorID == messageModel.CompetitorInfo.CompetitorID
                                                                    }

                                                                if (findResult != null) {
                                                                    mainViewModel.competitorInfoAll.value?.CompetitorInfo?.remove(
                                                                        findResult
                                                                    )

                                                                    CompetitorInfoAllManager.saveAsync(
                                                                        mainViewModel.competitorInfoAll.value
                                                                    )
                                                                }

                                                                if (!Controller.next(
                                                                        mainViewModel,
                                                                        mainActivity,
                                                                        forDeleteCompetitorInfo = true
                                                                    )
                                                                )
                                                                    Controller.haveABreak(
                                                                        mainViewModel,
                                                                        mainActivity, true
                                                                    ) //休息一下
                                                            }
                                                        }
                                                        //监听右边（正）button点击事件
                                                        .setPositiveButton(
                                                            R.string.button_text_no
                                                        ) { _, _ ->
                                                            ExceptionHandlerUtil.usingExceptionHandler {
                                                                validateRowInApp.ScoreValue =
                                                                    "" //清空确认，表示未确认成绩。
                                                                //定位到第一条分数为空的记录上，并设置此记录为当前记录。
                                                                Controller.goToFirstEmptyScoreAndSetCurrent(
                                                                    this,
                                                                    recyclerView
                                                                )
                                                            }
                                                        }
                                                        .setCancelable(true) //设置对话框是可取消的

                                                val dialog2 = builder2.create()
                                                dialog2.show()
                                                //endregion
                                            }
                                        }
                                        //监听右边（正）button点击事件
                                        .setPositiveButton(
                                            R.string.button_text_no
                                        ) { _, _ ->
                                            ExceptionHandlerUtil.usingExceptionHandler {
                                                validateRowInApp.ScoreValue = "" //清空确认，表示未确认成绩。
                                                //定位到第一条分数为空的记录上，并设置此记录为当前记录。
                                                Controller.goToFirstEmptyScoreAndSetCurrent(
                                                    this,
                                                    recyclerView
                                                )
                                            }
                                        }
                                        .setCancelable(true) //设置对话框是可取消的

                                val dialog = builder.create()
                                dialog.show()
                                //endregion
                            }
                        }
                    }
                    //endregion
                }
                //endregion
            }
        }
    }
    //endregion
}