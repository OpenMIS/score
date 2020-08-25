package com.game.score.core

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import com.game.score.Controller
import com.game.score.MainActivity
import com.game.score.models.xml.receive.CompetitorInfoAll
import com.game.score.ui.main.MainViewModel
import kotlinx.coroutines.GlobalScope
import java.io.File

object CompetitorInfoAllManager {
    var MainActivity: MainActivity? = null
    var MainViewModel: MainViewModel? = null

    //region 工具集
    private fun getFile(): File {
        return File(getFilePath())
    }
    //endregion

    @SuppressLint("SdCardPath")
    fun getFilePath(): String {
        return String.format(
            "/sdcard/Android/data/%s/%s.xml",
            MainActivity!!.packageName, CompetitorInfoAll::class.simpleName
        )
    }

    //region 尝试从SD卡里的CompetitorInfoAll.xml文件里载入需要打分的信息。（如果需要的话）
    /**
     * 尝试从SD卡里的CompetitorInfoAll.xml文件里载入需要打分的信息。（如果需要的话）
     */
    fun loadIfNeedAsync(handler: Handler) {
        val mainViewModel = MainViewModel!!
        if (mainViewModel.competitorInfoAll.value == null) {
            val file = getFile()
            val exists = file.exists()
            if (exists) {
                val temp = GameSettingsUtil.getHaveABreak(MainActivity!!)
                if (temp != MainViewModel!!.haveABreak.value)
                    MainViewModel!!.haveABreak.value = temp
            }

            GlobalScope.run {
                ExceptionHandlerUtil.usingExceptionHandler {
                    val xmlContent: String
                    if (exists) {
                        xmlContent = file.readText(Charsets.UTF_8)
                        val message = Message()
                        message.obj = XmlMappers.receive.readValue(
                            xmlContent,
                            CompetitorInfoAll::class.java
                        ) as CompetitorInfoAll

                        handler.sendMessage(message)
                    }
                }
            }
        }
    }
    //endregion

    //region 把收到的CompetitorInfoAll消息更新到视图模型，并且保存到SD卡里的CompetitorInfoAll.xml
    /**
     * 把收到的CompetitorInfoAll消息更新到视图模型，并且保存到SD卡里的CompetitorInfoAll.xml
     */
    fun update(competitorInfoAll: CompetitorInfoAll) {
        if (//【注意】可能收到的competitorInfoAll.CompetitorInfo为空值
            competitorInfoAll.CompetitorInfo != null &&
            competitorInfoAll.CompetitorInfo!!.count() > 0
        ) {
            val mainViewModel = MainViewModel!!

            //取消“消息一下”，进去工作状态。
            mainViewModel.haveABreak.value = false

            //region 更新mainViewModel.competitorInfoAll
            if (mainViewModel.competitorInfoAll.value == null)
                mainViewModel.competitorInfoAll.value = competitorInfoAll
            else {
                competitorInfoAll.CompetitorInfo?.forEach { it ->
                    val findResult =
                        mainViewModel.competitorInfoAll.value!!.CompetitorInfo?.find { inViewModel ->
                            inViewModel.CompetitorID == it.CompetitorID
                        }

                    if (findResult != null) {
                        //如果在视图模型里存在，则更新。
                        findResult.CompetitorName = it.CompetitorName
                        findResult.Event = it.Event

                        //region 更新分数列表里，除分数外的其他信息。
                        it.Score?.forEach { score ->
                            val findScoreResult =
                                findResult.Score?.find { inViewModel ->
                                    inViewModel.ScoreID == score.ScoreID
                                }

                            if (findScoreResult != null) {
                                //如果在视图模型里存在，则更新。
                                findScoreResult.ScoreName = score.ScoreName
                            } else {
                                //如果在视图模型里不存在，则添加。
                                if (findResult.Score == null)
                                    findResult.Score =
                                        mutableListOf()

                                findResult.Score!!.add(score.copy())
                            }
                        }
                        //endregion
                    } else {
                        if (mainViewModel.competitorInfoAll.value!!.CompetitorInfo == null)
                            mainViewModel.competitorInfoAll.value!!.CompetitorInfo
                                ?: mutableListOf()
                        //如果在视图模型里不存在，则添加。
                        mainViewModel.competitorInfoAll.value!!.CompetitorInfo!!.add(
                            it.copy()
                        )
                    }
                }
            }
            //endregion

            saveAsync(mainViewModel.competitorInfoAll.value)

            Controller.updateMainViewModel(MainViewModel!!, MainActivity!!)
        }
    }
    //endregion

    //region 异步保存到SD卡里的CompetitorInfoAll.xml
    /**
     * 异步保存到SD卡里的CompetitorInfoAll.xml
     */
    fun saveAsync(competitorInfoAll: CompetitorInfoAll?) {
        GlobalScope.run {
            ExceptionHandlerUtil.usingExceptionHandler {
                val file = getFile()

                if (competitorInfoAll?.CompetitorInfo == null ||
                    competitorInfoAll.CompetitorInfo!!.count() == 0
                ) {
                    clear(file)
                } else {
                    val xmlContent: String =
                        XmlMappers.send.writeValueAsString(competitorInfoAll)
                    file.writeText(xmlContent, Charsets.UTF_8) //保存到SD卡里
                }
            }
        }
    }
    //endregion

    private fun clear(file: File? = null) {
        val file2 = file ?: getFile()
        if (file2.exists()) file2.delete()
    }
}