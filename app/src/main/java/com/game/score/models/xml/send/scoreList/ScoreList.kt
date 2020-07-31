package com.game.score.models.xml.send.scoreList

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class ScoreList(
    val CompetitorID: String,

    @JacksonXmlProperty(localName = "Score")
    val Scores: List<Score>
)