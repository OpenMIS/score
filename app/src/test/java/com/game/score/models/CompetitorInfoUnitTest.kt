package com.game.score.models

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule
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
//            val xml: String = receive.writeValueAsString(node)
//            println(xml)

            val mapper = XmlMapper()
                //与下句registerModule(JaxbAnnotationModule())等效
                //.setAnnotationIntrospector(JaxbAnnotationIntrospector.nopInstance())
                .registerModule(JaxbAnnotationModule())

            val xml: String = mapper.writeValueAsString(node)
            println(xml)
        }
    }
}
