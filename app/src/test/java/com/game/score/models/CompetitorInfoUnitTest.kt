package com.game.score.models

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.Test
import java.io.File
import java.nio.file.Paths

class CompetitorInfoUnitTest {
    @Test
    fun test() {
        val mapper = XmlMapper().registerKotlinModule()
        val xmlFile = Paths.get(System.getProperty("user.dir"), """sampledata\CompetitorInfo.xml""")

        val node = mapper.readValue(File(xmlFile.toString()), CompetitorInfo::class.java)

        println(node)
    }
}
