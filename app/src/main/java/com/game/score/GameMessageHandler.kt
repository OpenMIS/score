package com.game.score

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.game.score.core.ExceptionHandlerUtil
import com.game.score.core.IGameMessageHandler
import com.game.score.core.IGameMessageModel
import com.game.score.core.sendInUI
import com.game.score.models.xml.receive.CompetitorInfo
import com.game.score.models.xml.receive.ScoreResponse
import com.game.score.models.xml.send.CompetitorInfoResponse
import com.game.score.ui.main.MainViewModel

/**
 * 竞赛消息处理
 */
object GameMessageHandler : IGameMessageHandler {

    //region 字段
    private lateinit var _appCompatActivity: AppCompatActivity
    private lateinit var _mainViewModel: MainViewModel
    //endregion

    //region 工具集
    //region 处理CompetitorInfo消息
    /**
     * 处理CompetitorInfo消息
     */
    private fun handleCompetitorInfo(messageModel: CompetitorInfo) {
        with(_mainViewModel) {
            if (messageModel.CompetitorInfo.Score != null &&
                messageModel.CompetitorInfo.Score!!.count() > 0 &&
                //仅在本地没有打分的情况下，才把从服务端收到的分数列表更新到app。
                competitorInfo.value?.CompetitorInfo?.Score == null
            ) {//服务端发来带分数列表的xml视为需要打分操作。并且 仅在本地没有打分的情况下，才把从服务端收到的分数列表更新到app。
                //region 服务端发来带分数列表的xml视为需要打分操作
                //TODO：以后等服务端支持不用等待一个打分慢的裁判时，再把下面条件打开。
//                val remainMustScoredCount = competitorInfo.value?.remainMustScoredCount()
//                if (remainMustScoredCount == null || remainMustScoredCount == 0 ||
//                messageModel.CompetitorInfo.CompetitorID == competitorInfo.value?.CompetitorInfo?.CompetitorID
//                ) { //说明没有正在打分 或者 是 同一个场次里的运动员或组合
                competitorName_Normal.value = true
                (messageModel.CompetitorInfo.Event + messageModel.CompetitorInfo.Phase).let {
                    if (it.isNotBlank()) eventAndPhase.value = it
                }

                messageModel.CompetitorInfo.CompetitorName.let {
                    if (it.isNotBlank() ||
                        competitorName.value == _appCompatActivity.getString(R.string.validate_success_competitorName)
                    )
                        competitorName.value = it
                }

                judgeName.value = messageModel.CompetitorInfo.JudgeName

                var changeIndex = 0
                if (currentScoreIndex.value != null &&
                    currentScoreIndex.value!! >= 0 &&
                    currentScoreIndex.value!! < messageModel.CompetitorInfo.Score!!.count()
                )
                    changeIndex = currentScoreIndex.value!!

                currentScore.value = messageModel.CompetitorInfo.Score!![changeIndex]
                currentScoreIndex.value = changeIndex

                //【注意】必须先设置currentScoreIndex，因为下句会触发分数List绑定。 List绑定时，使用到currentScoreIndex。
                competitorInfo.value = messageModel
//                }
//                else {//说明正在打分
//                    //当裁判本场被打分完时，先保存其他场需要的打分
//                }
                //endregion
            } else if (messageModel.CompetitorInfo.CompetitorID.isBlank()) {//视为 服务端在打分器监控台 点击Break
                //region 休息一下
                haveABreak(this) //休息一下
                //endregion
            }

            //回应收到消息
            CompetitorInfoResponse().sendInUI()
        }
    }

    //region 处理ScoreResponse消息
    /**
     * 处理ScoreResponse消息
     */
    private fun handleScoreResponse(messageModel: ScoreResponse) {
        with(_mainViewModel) {
            //region ScoreResponse消息处理
            if (competitorInfo.value != null &&
                competitorInfo.value!!.CompetitorInfo.Score != null && //表示需要打分

                //同一个运动员 或者 同一组（多人项目）
                competitorInfo.value!!.CompetitorInfo.CompetitorID ==
                messageModel.CompetitorInfo.CompetitorID
            ) {
                var change = false
                messageModel.CompetitorInfo.Score?.forEach { score ->
                    competitorInfo.value!!.CompetitorInfo.Score!!.find {
                        it.ScoreID == score.ScoreID
                    }?.let {
                        if (it.ScoreStatus != score.ScoreStatus) {
                            it.ScoreStatus = score.ScoreStatus
                            change = true
                        }

                        if (it.ScoreErrorMessage != score.ScoreErrorMessage) {
                            it.ScoreErrorMessage = score.ScoreErrorMessage
                            change = true
                        }

                        //【注意】由于服务端回应的分数可能为空的，所以需要判断一下。
                        if (!score.ScoreValue.isBlank() && it.ScoreValue != score.ScoreValue) {
                            it.ScoreValue = score.ScoreValue
                            change = true
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
                        competitorInfo.value?.CompetitorInfo?.Score?.indexOfFirst {
                            it.ScoreID == firstErrorScore.ScoreID
                        }

                    if (errorIndex != null && errorIndex >= 0) { //找到错误的分数
                        currentScoreIndex.value = errorIndex
                        currentScore.value =
                            competitorInfo.value!!.CompetitorInfo.Score!![errorIndex]

                        //region 定位到第一条错误的分数上
                        val recyclerView =
                            _appCompatActivity.findViewById<RecyclerView>(R.id.score_list)
                        //定位到指定项如果该项可以置顶就将其置顶显示。比如:微信联系人的字母索引定位就是采用这种方式实现。
                        (recyclerView.layoutManager as LinearLayoutManager?)!!.scrollToPositionWithOffset(
                            errorIndex,
                            /*距离顶部的像素。通过此值，让正在打分的项尽量列表的上下的中间位置，
                            这样方便看到之前打分与之后要打的分。
                            */
                            100
                        )
                        //endregion

                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
                //endregion

                //region 确认成绩的回应
                val validateRowInApp = competitorInfo.value?.CompetitorInfo?.Score?.find {
                    it.ScoreID == ScoreConsts.Attribute_F_Status && it.ScoreValue == ScoreConsts.Status_ScoreValue_Validate
                }

                if (validateRowInApp != null) {
                    if (messageModel.CompetitorInfo.Score == null) {//服务端确认成绩成功
                        haveABreak(this) //休息一下

                        Toast.makeText(
                            _appCompatActivity,
                            R.string.validate_success,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {//服务端确认成绩失败
                        Toast.makeText(
                            _appCompatActivity,
                            R.string.validate_Fail,
                            Toast.LENGTH_LONG
                        ).show()

                        if (firstErrorScore != null)  //说明分数有错误
                            validateRowInApp.ScoreValue = "" //清空确认，表示未确认成绩。
                        else {
                            //region 提示是否强制跳过打分
                            val remainMustScoredCount =
                                competitorInfo.value?.remainMustScoredCount()
                            val emptyScoreValueCountString =
                                if (remainMustScoredCount != null && remainMustScoredCount > 0)
                                    _appCompatActivity.getString(
                                        R.string.alertDialog_message_confirm_NoScoreValueCount,
                                        remainMustScoredCount
                                    )
                                else ""

                            val message =
                                emptyScoreValueCountString + _appCompatActivity.getString(
                                    R.string.alertDialog_message_ForceSkipScoring
                                )
                            val recyclerView =
                                _appCompatActivity.findViewById<RecyclerView>(R.id.score_list)

                            val builder =
                                AlertDialog.Builder(_appCompatActivity)
                                    .setTitle(R.string.alertDialog_title_confirm)
                                    .setMessage(message)
                                    .setPositiveButton(
                                        R.string.button_text_no
                                    ) { _, _ ->
                                        ExceptionHandlerUtil.usingExceptionHandler {
                                            validateRowInApp.ScoreValue = "" //清空确认，表示未确认成绩。
                                            //定位到第一条分数为空的记录上，并设置此记录为当前记录。
                                            Controller.goToFirstEmptyAndSetCurrent(
                                                this,
                                                recyclerView
                                            )
                                        }
                                    }
                                    //监听下方button点击事件
                                    .setNegativeButton(R.string.button_text_yes) { _, _ ->
                                        ExceptionHandlerUtil.usingExceptionHandler {
                                            //region 再次确认
                                            val builder2 =
                                                AlertDialog.Builder(_appCompatActivity)
                                                    .setTitle(R.string.alertDialog_title_confirmAgain)
                                                    .setMessage(message)
                                                    .setPositiveButton(
                                                        R.string.button_text_no
                                                    ) { _, _ ->
                                                        ExceptionHandlerUtil.usingExceptionHandler {
                                                            validateRowInApp.ScoreValue =
                                                                "" //清空确认，表示未确认成绩。
                                                            //定位到第一条分数为空的记录上，并设置此记录为当前记录。
                                                            Controller.goToFirstEmptyAndSetCurrent(
                                                                this,
                                                                recyclerView
                                                            )
                                                        }
                                                    }
                                                    //监听下方button点击事件
                                                    .setNegativeButton(R.string.button_text_yes) { _, _ ->
                                                        ExceptionHandlerUtil.usingExceptionHandler {
                                                            clearAll() //清除所有信息
                                                            competitorName_Normal.value =
                                                                false //表示在competitorName文本框显示“服务端确认成绩成功”相关消息

                                                            competitorName.value =
                                                                _appCompatActivity.getString(R.string.validate_success_competitorName)
                                                        }
                                                    }.setCancelable(true) //设置对话框是可取消的

                                            val dialog2 = builder2.create()
                                            dialog2.show()
                                            //endregion
                                        }
                                    }.setCancelable(true) //设置对话框是可取消的

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
    //endregion

    //region 休息一下
    /**
     * 休息一下
     */
    private fun haveABreak(viewModel: MainViewModel) {
        with(viewModel) {
            clearAll() //清除所有信息
            competitorName_Normal.value =
                false //表示在competitorName文本框显示“服务端确认成绩成功”相关消息

            competitorName.value =
                _appCompatActivity.getString(R.string.validate_success_competitorName)
        }
    }
    //endregion
    //endregion

    //region 初始化
    /**
     * 初始化
     */
    fun init(
        appCompatActivity: AppCompatActivity,
        mainViewModel: MainViewModel
    ) {
        _appCompatActivity = appCompatActivity
        _mainViewModel = mainViewModel
    }
    //endregion

    //region 处理消息
    /**
     * 处理消息
     */
    override fun handle(messageModel: IGameMessageModel) {
        when (messageModel) {
            is CompetitorInfo -> handleCompetitorInfo(messageModel)
            is ScoreResponse -> handleScoreResponse(messageModel)
        }
    }
    //endregion
}