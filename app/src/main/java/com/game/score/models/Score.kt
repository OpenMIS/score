package com.game.score.models

/**
 * 数据模型 - 分数
 */
data class Score(
    /**
     * 场次里的步骤的顺序。
     *
     * 比如：1
     */
    val playOrder: Int,

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
     * 空，表示还未打分。
     */
    val ScoreValue: String,

    val ScoreErrorMessage: String,

    val ScoreStatus: String
)