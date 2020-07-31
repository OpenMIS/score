package com.game.score.models.xml.send

import com.game.score.core.GameMessageModel
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "Body")
@XmlAccessorType(XmlAccessType.FIELD)
data class CompetitorInfoResponse(
    /**
     * 消息类型
     */
    @XmlAttribute
    override val MessageType: String
) : GameMessageModel