package com.game.score.core

/**
 * 分数工具
 */
class ScoreUtil {
    companion object {
        /**
         * 是否打分。
         *
         * 比如：F_1 到 F_nn
         */
        @JvmStatic
        fun isScoring(scoreID: String): Boolean =
            scoreID.matches("""^F_\d+$""".toRegex())
    }
}