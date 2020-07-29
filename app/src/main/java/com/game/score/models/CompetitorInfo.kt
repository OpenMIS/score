package com.game.score.models

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

/**
 * 数据模型 - 分数
 */
//@XmlRootElement(name = "CompetitorInfo")
@JacksonXmlRootElement(localName = "CompetitorInfo2")
data class CompetitorInfo(
    @JacksonXmlProperty(isAttribute = false)
    val Event: String,

    @JacksonXmlProperty(isAttribute = true)
    val Phase: String,

    @JacksonXmlProperty(isAttribute = true)
    val CompetitorName: String,

    @JacksonXmlProperty(isAttribute = true)
    val CompetitorID: String,

    @JacksonXmlProperty(isAttribute = true)
    val JudgeName: String//,

//    @JacksonXmlElementWrapper(useWrapping = false)
//    val Score: List<Score>
)