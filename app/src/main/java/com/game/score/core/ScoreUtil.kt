package com.game.score.core

import com.game.score.ScoreConsts

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

        /**
         * 是否有效打分。
         *
         * 仅在app端判断小数，超范围的验证在服务端。
         */
        @JvmStatic
        fun isVaild(scoreID: String, scoreValue: String): Boolean {
            var result = true

            //【注意】百分比扣分，有些赛事的小数可能不是0或5
            if (isScoring(scoreID) && scoreID != ScoreConsts.Attribute_F_100) {
                result = if (scoreID == ScoreConsts.Attribute_F_0)
                    scoreValue.matches("""^\d+$""".toRegex())
                else scoreValue.matches("""^\d+(\.[05])?$""".toRegex())
            }

            return result
        }
    }
}