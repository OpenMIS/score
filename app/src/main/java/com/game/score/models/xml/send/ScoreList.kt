package com.game.score.models.xml.send

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.game.score.models.xml.send.scoreList.Score

data class ScoreList(
    val CompetitorID: String,

    @JacksonXmlProperty(localName = "Score")
    val Scores: List<Score>
)