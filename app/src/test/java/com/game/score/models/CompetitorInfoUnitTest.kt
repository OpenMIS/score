package com.game.score.models

import com.game.score.core.GameMessageUtil
import com.game.score.core.XmlMappers
import com.game.score.models.xml.receive.CompetitorInfo
import org.junit.Test
import java.io.File
import java.nio.file.Paths

class CompetitorInfoUnitTest {
    @Test
    fun test() {
        val xmlFile = Paths.get(
            System.getProperty("user.dir"),
            """sampledata\xml\receive\CompetitorInfo.xml"""
        )

        val gameMessage =
            GameMessageUtil.convertFrom(File(xmlFile.toString()).readText(Charsets.UTF_8))
        //val node = mapper.readValue(File(xmlFile.toString()), CompetitorInfo::class.java)

        with(XmlMappers) {
            val class1 =
                Class.forName("com.game.score.models.xml.receive." + gameMessage.messageType)
            //动态类型
            val node = receive.readValue(gameMessage.messageContent, class1) as CompetitorInfo

            println(node)

            println("从对象生成的XML：")

            val xml: String = GameMessageUtil.toOriginalXml(node)
            println(xml)
        }
    }
}
