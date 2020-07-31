package com.game.score.models.xml.send

import com.game.score.core.GameMessageModel
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "Body")
@XmlAccessorType(XmlAccessType.FIELD)
data class HeartBeat(
    /**
     * 消息类型
     */
    @XmlAttribute
    override val MessageType: String,

    val HeartBeat: HeartBeatClass
) : GameMessageModel {
    @XmlAccessorType(XmlAccessType.FIELD)
    data class HeartBeatClass(

        @XmlAttribute
        val Battery: Int,

        @XmlAttribute
        val RcvPort: Int,

        @XmlAttribute
        val Version: String
    )
}