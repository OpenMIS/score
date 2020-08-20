package com.game.score.models.xml.send

import com.game.score.core.IGameMessageModel
import com.game.score.core.IGameSendMessageModel
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "Body")
@XmlAccessorType(XmlAccessType.FIELD)
data class CompetitorInfoAllResponse(
    /**
     * 消息类型
     */
    @XmlAttribute
    override val MessageType: String = "CompetitorInfoAllResponse"
) : IGameMessageModel, IGameSendMessageModel {
    /**
     * 裁判ID。
     *
     * 实际数据：1-3。
     *
     * 与设置文件里的ClientID对应。
     *
     * 发送时自动设置此值。
     */
    @XmlAttribute
    override var JudgeID: Int = 0
}