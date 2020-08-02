package com.game.score.core

import com.game.score.core.ExceptionHandlerUtil.Companion.usingExceptionHandler

class ScoreUtil {
    companion object {
        @JvmStatic
        fun getOrder(scoreID: String): String {
            var result = ""
            usingExceptionHandler {
                if (!scoreID.isBlank())
                    result = when (scoreID) {
                        "F_TotalScore", "F_Status" -> ""
                        else -> if (scoreID.startsWith("F_")) scoreID.substring(2) else scoreID
                    }
            }

            return result
        }
    }
}