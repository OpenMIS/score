package com.game.score.models.xml.receive

import com.game.score.core.IGameMessageModel
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "Body")
@XmlAccessorType(XmlAccessType.FIELD)
data class ScoreResponse(
    /**
     * 消息类型
     */
    @XmlAttribute
    override val MessageType: String,

    val CompetitorInfo: CompetitorInfoClass
) : IGameMessageModel {
    data class CompetitorInfoClass(
        /**
         * 【注意】此处含字符，所以使用字符串。
         * 比如：2-4982-1
         */
        @XmlAttribute
        val CompetitorID: String,

        /**
         * 分数列表。
         *
         * 可能为空。
         */
        val Score: MutableList<ScoreClass>?
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
            var ScoreValue: String,

            /**
             * 错误信息。
             *
             * 比如： 空字符串、Please Enter Score、Score out of Range
             */
            @XmlAttribute
            val ScoreErrorMessage: String,

            /**
             * 分数状态。
             *
             * 比如： 空字符串、Done、Error
             */
            @XmlAttribute
            val ScoreStatus: String
        )
    }
}