package com.game.score.models.xml.send

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.game.score.core.IGameMessageModel
import com.game.score.core.IGameSendMessageModel
import com.game.score.core.ScoreUtil
import javax.xml.bind.annotation.*

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
    @XmlAccessorType(XmlAccessType.FIELD)
    data class ScoreListClass(
        @XmlAttribute
        val CompetitorID: String,

        @JacksonXmlProperty(localName = "Score")
        @XmlAttribute
        val Scores: MutableList<Score>
    ) {
        @XmlAccessorType(XmlAccessType.FIELD)
        data class Score(
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
            var ScoreValue: String
        ) {
            @XmlTransient
            fun getOrder(): String = ScoreUtil.getOrder(ScoreID)
        }
    }
}