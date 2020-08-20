package com.game.score.core

import android.annotation.SuppressLint
import com.game.score.MainActivity
import com.game.score.models.xml.receive.CompetitorInfoAll
import java.io.File

object CompetitorInfoAllManager {
    var MainActivity: MainActivity? = null

    @SuppressLint("SdCardPath")
    fun getFilePath(): String {
        return String.format(
            "/sdcard/Android/data/%s/%s.xml",
            MainActivity!!.packageName, CompetitorInfoAll::class.simpleName
        )
    }

    fun getFile(): File {
        return File(getFilePath())
    }

    fun update(competitorInfoAll: CompetitorInfoAll) {

    }
}