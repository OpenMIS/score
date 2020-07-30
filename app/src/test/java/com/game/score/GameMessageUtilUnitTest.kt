package com.game.score

import com.game.score.core.GameMessageUtil
import org.junit.Test
import java.io.File
import java.nio.file.Paths

class GameMessageUtilUnitTest {
    @Test
    fun convertFromTest() {
        val xmlFile =
            Paths.get(
                System.getProperty("user.dir"),
                """sampledata\xml\original\CompetitorInfo.xml"""
            )
        val content = File(xmlFile.toString()).readText()
        val testResult = GameMessageUtil.convertFrom(content)

        println(testResult)
    }
}
