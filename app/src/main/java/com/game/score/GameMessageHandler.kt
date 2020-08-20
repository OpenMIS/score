package com.game.score

import android.annotation.SuppressLint
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.game.score.core.*
import com.game.score.models.xml.receive.CompetitorInfo
import com.game.score.models.xml.receive.CompetitorInfoAll
import com.game.score.models.xml.receive.HeartBeatResponse
import com.game.score.models.xml.receive.ScoreResponse
import com.game.score.models.xml.send.CompetitorInfoAllResponse
import com.game.score.models.xml.send.CompetitorInfoResponse
import com.game.score.ui.main.MainViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.commons.lang3.time.DurationFormatUtils
import java.util.*

/**
 * 竞赛消息处理
 */
object GameMessageHandler : IGameMessageHandler {
    //region 字段
    private lateinit var _mainActivity: MainActivity
    private lateinit var _mainViewModel: MainViewModel
    private var _lastHeartBeat: Date? = null
    private var _startStatusCheck = false
//endregion

    //region 工具集
    private fun startStatusCheckIfNeed() {
        if (!_startStatusCheck) {
            _startStatusCheck = true

            GlobalScope.launch {
                while (true) {
                    delay(3000L)
                    ExceptionHandlerUtil.usingExceptionHandler {
                        if (_lastHeartBeat == null ||
                            DurationFormatUtils.formatPeriod(
                                _lastHeartBeat!!.time,
                                Date().time,
                                "s"
                            )
                                .toInt() > 7 //7秒钟以上没有收到心跳回应包
                        )
                            _mainActivity.handler.sendEmptyMessage(1)
                    }
                }
            }
        }
    }

    //region 处理消息
    //region 处理CompetitorInfoAll消息
    /**
     * 处理CompetitorInfoAll消息
     */
    @SuppressLint("SdCardPath")
    private fun handleCompetitorInfoAll(messageModel: CompetitorInfoAll) {
        //把收到的CompetitorInfoAll消息更新到视图模型与SD卡里的CompetitorInfoAll.xml
        CompetitorInfoAllManager.update(messageModel)

        //回应收到消息
        CompetitorInfoAllResponse().sendInUI()
    }
    //endregion

    //region 处理CompetitorInfo消息
    /**
     * 处理CompetitorInfo消息
     */
    private fun handleCompetitorInfo(messageModel: CompetitorInfo) {
        with(_mainViewModel) {
            if (messageModel.CompetitorInfo.CompetitorID.isBlank()) {//视为 服务端在打分器监控台 点击Break
                //region 休息一下
                haveABreak(this) //休息一下
                //endregion
            } else {
                haveABreak.value = false
                Controller.updateMainUI(_mainViewModel, _mainActivity)
            }

            //回应收到消息
            CompetitorInfoResponse().sendInUI()
        }
    }
    //endregion

//region 处理ScoreResponse消息
    /**
     * 处理ScoreResponse消息
     */
    private fun handleScoreResponse(messageModel: ScoreResponse) {
        with(_mainViewModel) {
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
                            _mainActivity.findViewById<RecyclerView>(R.id.score_list)
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
                val validateRowInApp = currentCompetitorInfo.value?.Score?.find {
                    it.ScoreID == ScoreConsts.Attribute_F_Status && it.ScoreValue == ScoreConsts.Status_ScoreValue_Validate
                }

                if (validateRowInApp != null) {
                    if (messageModel.CompetitorInfo.Score == null) {//服务端确认成绩成功
                        haveABreak(this) //休息一下

                        Toast.makeText(
                            _mainActivity,
                            R.string.validate_success,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {//服务端确认成绩失败
                        Toast.makeText(
                            _mainActivity,
                            R.string.validate_Fail,
                            Toast.LENGTH_LONG
                        ).show()

                        if (firstErrorScore != null)  //说明分数有错误
                            validateRowInApp.ScoreValue = "" //清空确认，表示未确认成绩。
                        else {
                            //region 提示是否强制跳过打分
                            val remainMustScoredCount =
                                currentCompetitorInfo.value?.remainMustScoredCount()
                            val emptyScoreValueCountString =
                                if (remainMustScoredCount != null && remainMustScoredCount > 0)
                                    _mainActivity.getString(
                                        R.string.alertDialog_message_confirm_NoScoreValueCount,
                                        remainMustScoredCount
                                    )
                                else ""

                            val message =
                                emptyScoreValueCountString + _mainActivity.getString(
                                    R.string.alertDialog_message_ForceSkipScoring
                                )
                            val recyclerView =
                                _mainActivity.findViewById<RecyclerView>(R.id.score_list)

                            val builder =
                                AlertDialog.Builder(_mainActivity)
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
                                                AlertDialog.Builder(_mainActivity)
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
                                                                _mainActivity.getString(R.string.validate_success_competitorName)
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

//region 处理HeartBeatResponse消息
    /**
     * 处理HeartBeatResponse消息
     */
    private fun handleHeartBeatResponse() {
        _lastHeartBeat = Date()

        with(_mainActivity.findViewById<TextView>(R.id.textView_appStatus)) {
            _mainViewModel.appStatus.value = _mainActivity.getString(R.string.app_status_online)
            setTextColor(ContextCompat.getColor(_mainActivity, R.color.colorAppStatus_Online))
        }
    }
//endregion
    //endregion

//region 休息一下
    /**
     * 休息一下
     */
    private fun haveABreak(viewModel: MainViewModel) {
        with(viewModel) {
            haveABreak.value = true
            clearAll(false) //清除所有信息
            competitorName_Normal.value =
                false //表示在competitorName文本框显示“服务端确认成绩成功”相关消息

            competitorName.value =
                _mainActivity.getString(R.string.validate_success_competitorName)
        }
    }
//endregion
//endregion

//region 初始化
    /**
     * 初始化
     */
    fun init(
        appCompatActivity: MainActivity,
        mainViewModel: MainViewModel
    ) {
        _mainActivity = appCompatActivity
        _mainViewModel = mainViewModel

        startStatusCheckIfNeed()
    }
//endregion

//region 处理消息
    /**
     * 处理消息
     */
    override fun handle(messageModel: IGameMessageModel) {
        when (messageModel) {
            is CompetitorInfoAll -> handleCompetitorInfoAll(messageModel)
            is CompetitorInfo -> handleCompetitorInfo(messageModel)
            is ScoreResponse -> handleScoreResponse(messageModel)
            is HeartBeatResponse -> handleHeartBeatResponse()
        }
    }
//endregion
}