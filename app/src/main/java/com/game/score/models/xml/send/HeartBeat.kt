package com.game.score.models.xml.send

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute

@XmlAccessorType(XmlAccessType.FIELD)
data class HeartBeat(

    @XmlAttribute
    val Battery: Int,

    @XmlAttribute
    val RcvPort: Int,

    @XmlAttribute
    val Version: String
)