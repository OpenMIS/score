package com.game.score.models

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
    val ScoreID: String,

    /**
     * 分数的名称。
     *
     * 比如：立定敬礼
     */
    val ScoreName: String,

    /**
     * 得分。
     *
     * 空字符串表示还未打分。
     */
    var ScoreValue: String,

    /**
     * 错误信息。
     *
     * 比如： 空字符串、Please Enter Score、Score out of Range
     */
    val ScoreErrorMessage: String,

    /**
     * 分数状态。
     *
     * 比如： 空字符串、Done、Error
     */
    val ScoreStatus: String
) {
    val order: String
        get() =
            when (ScoreID) {
                "F_TotalScore", "F_Status" -> ""
                else -> ScoreID.substring(2)
            }
}