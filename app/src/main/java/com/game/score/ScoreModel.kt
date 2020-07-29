package com.game.score

/**
 * 数据模型 - 分数
 */
data class ScoreModel(
    val order: Int, val name: String, val score: Float?,
    val hasVerifyScoreError: Boolean = false, val isSendFail: Boolean = false
) {
    override fun toString(): String = name
}