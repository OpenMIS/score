package com.game.score.core

import com.game.score.core.ExceptionHandlerUtil.Companion.usingExceptionHandler

class ScoreUtil {
    companion object {
        @JvmStatic
        fun getOrder(scoreID: String): String {
            var result = ""
            //由于此方法，在layout布局xml的数据绑定直接调用，所以也做一下异常处理
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