package com.game.score.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

//@JacksonXmlRootElement(localName = "CompetitorInfo")
//@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIgnoreProperties(value = ["MessageType"])
data class CompetitorInfo(

    //val messageType: String,
    val event: String,

    val phase: String,

    val competitorName: String,

    val competitorID: String,

    val judgeName: String,

    @JacksonXmlProperty(localName = "Score")
    val scores: List<Score>
)