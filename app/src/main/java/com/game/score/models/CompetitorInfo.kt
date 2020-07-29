package com.game.score.models

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class CompetitorInfo(

    val Event: String,

    val Phase: String,

    val CompetitorName: String,

    val CompetitorID: String,

    /**
     * 裁判名称。
     *
     * 马术比如：E、M、C
     */
    val JudgeName: String,

    @JacksonXmlProperty(localName = "Score")
    val Scores: List<Score>
)