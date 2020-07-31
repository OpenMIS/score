package com.game.score.models.xml.receive

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.game.score.models.xml.receive.competitorInfo.Score
import javax.xml.bind.annotation.XmlAttribute

data class CompetitorInfo(
    @XmlAttribute
    val Event: String,

    @XmlAttribute
    val Phase: String,

    @XmlAttribute
    val CompetitorName: String,

    /**
     * 【注意】此处含字符，所以使用字符串。
     */
    @XmlAttribute
    val CompetitorID: String,

    /**
     * 裁判名称。
     *
     * 马术比如：E、M、C
     */
    @XmlAttribute
    val JudgeName: String,

    @JacksonXmlProperty(localName = "Score")
    val Scores: List<Score>
)