package com.game.score.core

class ScoreUtil {
    companion object {
        @JvmStatic
        fun getOrder(scoreID: String) = when (scoreID) {
            "F_TotalScore", "F_Status" -> ""
            else -> scoreID.substring(2)
        }
    }
}