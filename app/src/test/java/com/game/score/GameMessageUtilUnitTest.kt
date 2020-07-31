package com.game.score

import androidx.lifecycle.MutableLiveData
import com.game.score.core.GameMessageUtil
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.nio.file.Paths

class GameMessageUtilUnitTest {
    @Test
    fun convertFrom_convertToTest() {
        val xmlFile =
            Paths.get(
                System.getProperty("user.dir"),
                """sampledata\xml\receive\CompetitorInfo.xml"""
            )
        val content = File(xmlFile.toString()).readText(Charsets.UTF_8)
        val testResult = GameMessageUtil.getMessageType(content)

        println(testResult)

        Assert.assertEquals("CompetitorInfo", testResult)

        var tt = Test2(MessageType = "aa")
        val a = MutableLiveData(tt)
        var b = MutableLiveData(tt)
        a.value!!.MessageType = "bb"

        println(b.value!!.MessageType)

    }
}

data class Test2(
    var MessageType: String
)
