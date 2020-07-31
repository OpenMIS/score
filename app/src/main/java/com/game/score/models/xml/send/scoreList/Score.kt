package com.game.score.models.xml.send.scoreList

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute

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
)