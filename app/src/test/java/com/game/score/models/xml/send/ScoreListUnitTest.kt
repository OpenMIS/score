package com.game.score.models.xml.send

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector
import com.game.score.core.GameMessageUtil
import com.game.score.core.XmlMappers
import org.junit.Test
import java.io.File
import java.nio.file.Paths

class ScoreListUnitTest {
    @Test
    fun test() {
        val xmlFileName = javaClass.simpleName.replace("UnitTest", "")
        val dirName = if (javaClass.name.contains("receive")) "receive" else "send"
        val xmlFile = Paths.get(
            System.getProperty("user.dir"),
            """sampledata\xml\$dirName\$xmlFileName.xml"""
        )
        val xmlContent = File(xmlFile.toString()).readText(Charsets.UTF_8)
        val messageType = GameMessageUtil.getMessageType(xmlContent)
        //val node = mapper.readValue(File(xmlFile.toString()), CompetitorInfo::class.java)

        with(XmlMappers) {
            val class1 =
                Class.forName("com.game.score.models.xml.$dirName.$messageType")
            //动态类型
            val gameMessageModel = receive.readValue(xmlContent, class1)

            println(gameMessageModel)

            println("从对象生成的XML：")

            val xml = send.writeValueAsString(gameMessageModel)
            println(xml)
        }
    }

    @Test
    fun tes2t() {
        val send =
            XmlMapper(JacksonXmlModule())
                .configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true)
                .setAnnotationIntrospector(JaxbAnnotationIntrospector()) //希望的结果


        println("构建的对象转xml：")
        val xml2 = send.writeValueAsString(
            ScoreList(
                ScoreList = ScoreList.ScoreListClass(
                    "2-4982-1", mutableListOf(
                        ScoreList.ScoreListClass.ScoreClass(
                            ScoreID = "F_1",
                            ScoreValue = "8.1"
                        )
                    )
                )
            )
        )
        println(xml2)
    }
}
