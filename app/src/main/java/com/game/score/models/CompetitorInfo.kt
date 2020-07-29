package com.game.score.models

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

/**
 * 数据模型 - 分数
 */
//@XmlRootElement(name = "CompetitorInfo")
@JacksonXmlRootElement(localName = "CompetitorInfo")
data class CompetitorInfo(
    val event: String,

    val phase: String,

    val competitorName: String,

    val competitorID: String,

    val judgeName: String,

    @JacksonXmlProperty(localName = "Score")
    val scores: List<Score>
)