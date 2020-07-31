package com.game.score.models

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.game.score.core.GameMessageUtil
import com.game.score.core.XmlMappers
import com.game.score.models.xml.receive.CompetitorInfo
import com.game.score.models.xml.receive.CompetitorInfo2
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

    @Test
    fun test2() {
        /**
         * 用于接收消息处理的XmlMapper
         */
        val receive2 = XmlMapper(JacksonXmlModule().apply {
            setDefaultUseWrapper(false)
        }).registerKotlinModule()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true) //映射时不区分大小写
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) //忽略未知的XML元素或属性


        val xmlFile = Paths.get(
            System.getProperty("user.dir"),
            """sampledata\xml\receive\CompetitorInfo.xml"""
        )

        val gameMessage =
            GameMessageUtil.convertFrom(File(xmlFile.toString()).readText(Charsets.UTF_8))
        //val node = mapper.readValue(File(xmlFile.toString()), CompetitorInfo::class.java)

        with(XmlMappers) {
            val class1 =
                Class.forName("com.game.score.models.xml.receive." + gameMessage.messageType + "2")
            //动态类型
            val node = receive2.readValue(File(xmlFile.toString()), class1) as CompetitorInfo2

            println(node)

            println("从对象生成的XML：")

            val xml: String = send.writeValueAsString(node)
            println(xml)
        }
    }
}
