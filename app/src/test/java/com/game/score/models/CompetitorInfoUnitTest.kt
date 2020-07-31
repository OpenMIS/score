package com.game.score.models

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.Test
import java.io.File
import java.nio.file.Paths

class CompetitorInfoUnitTest {
    private val mapper = XmlMapper(JacksonXmlModule().apply {
        setDefaultUseWrapper(false)
    }).registerKotlinModule()
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true) //映射时不区分大小写
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) //忽略未知的XML元素或属性
        .configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true) //忽略根

    @Test
    fun test() {
        val xmlFile = Paths.get(
            System.getProperty("user.dir"),
            """sampledata\xml\modify\receive\CompetitorInfo.xml"""
        )

        //val node = mapper.readValue(File(xmlFile.toString()), CompetitorInfo::class.java)

        val class1 = Class.forName("com.game.score.models.xml.CompetitorInfo")
        //动态类型
        val node = mapper.readValue(File(xmlFile.toString()), class1) as CompetitorInfo

        println(node)
    }
}
