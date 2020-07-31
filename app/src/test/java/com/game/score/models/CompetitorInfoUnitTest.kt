package com.game.score.models

import com.game.score.core.GameMessageUtil
import com.game.score.core.XmlMappers
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
        val xmlContent = File(xmlFile.toString()).readText(Charsets.UTF_8)
        val messageType = GameMessageUtil.getMessageType(xmlContent)
        //val node = mapper.readValue(File(xmlFile.toString()), CompetitorInfo::class.java)

        with(XmlMappers) {
            val class1 =
                Class.forName("com.game.score.models.xml.receive.$messageType")
            //动态类型
            val gameMessageModel = receive.readValue(xmlContent, class1)

            println(gameMessageModel)

            println("从对象生成的XML：")

            val xml: String = send.writeValueAsString(gameMessageModel)
            println(xml)
        }
    }
}
