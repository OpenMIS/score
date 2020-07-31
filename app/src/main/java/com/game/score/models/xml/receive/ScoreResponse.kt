package com.game.score.models.xml.receive

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.game.score.models.xml.receive.scoreResponse.Score

@JacksonXmlRootElement(localName = "CompetitorInfo")
data class ScoreResponse(
    /**
     * 【注意】此处含字符，所以使用字符串。
     */
    val CompetitorID: String,

    @JacksonXmlProperty(localName = "Score")
    val Scores: List<Score>
)