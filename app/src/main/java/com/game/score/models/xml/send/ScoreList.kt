package com.game.score.models.xml.send

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.game.score.models.xml.send.scoreList.Score
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute

@XmlAccessorType(XmlAccessType.FIELD)
data class ScoreList(
    @XmlAttribute
    val CompetitorID: String,

    @JacksonXmlProperty(localName = "Score")
    @XmlAttribute
    val Scores: List<Score>
)