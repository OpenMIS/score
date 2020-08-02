package com.game.score.models.xml.send

import com.game.score.core.IGameMessageModel
import com.game.score.core.IGameSendMessageModel
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "Body")
@XmlAccessorType(XmlAccessType.FIELD)
data class ScoreList(
    /**
     * 消息类型
     */
    @XmlAttribute
    override val MessageType: String = "ScoreList",


    val ScoreList: ScoreListClass
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

    @XmlAccessorType(XmlAccessType.FIELD)
    data class ScoreListClass(
        @XmlAttribute
        val CompetitorID: String,

        val Score: MutableList<ScoreClass>
    ) {
        @XmlAccessorType(XmlAccessType.FIELD)
        data class ScoreClass(
            /**
             * 分数标识。
             *
             * 比如：F_0 .. F_n(最大大概36步)、F_TotalScore、F_Status
             *
             * F_TotalScore表示此条记录为总分
             *
             * F_Status表示validate（已经确认成绩）
             */
            @XmlAttribute
            val ScoreID: String,

            /**
             * 得分。
             *
             * 空字符串表示还未打分。
             */
            @XmlAttribute
            var ScoreValue: String
        )
    }
}