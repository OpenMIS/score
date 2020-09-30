package com.game.score.core

import com.game.score.ScoreConsts
import com.game.score.models.StepRange
import com.game.score.models.xml.receive.CompetitorInfoAll

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

        //region 装舞步配对赛：裁判选择打哪个表：A 1-10步、B 11-18步、C 19-23步。
        /**
         * 盛装舞步配对赛：裁判选择打哪段步伐：A 1-10步、B 11-18步、C 19-23步。
         *
         * @param competitorInfoAll 本场所有未打分运动员
         *
         * @param competitorInfo 当前在打分的运动员
         *
         * @return 步伐区段，空值表示所有步伐。
         */
        @JvmStatic
        fun drJudgeSelectStepRange(
            competitorInfoAll: CompetitorInfoAll?,
            competitorInfo: CompetitorInfoAll.CompetitorInfoClass?
        ): StepRange? {
            var result: StepRange? = null
            //region 装舞步配对赛
            val isDRPairMatch = competitorInfoAll?.IsDRPairMatch ?: false //是否装舞步配对赛
            val regex = """^F_(\d+)$""".toRegex()
            if (isDRPairMatch && competitorInfo?.Score != null) {
                result = StepRange(1, 10)
                //盛装舞步配对赛的裁判选择打哪个表：A 1-10、B 11-18、C 19-23表

                //计算已经打分的
                fun countScored(
                    score: CompetitorInfoAll.CompetitorInfoClass.ScoreClass,
                    start: Int, //开始步伐
                    end: Int //结束步伐
                ): Boolean {
                    var reuslt = false
                    val matchResult = regex.find(score.ScoreID)
                    if (matchResult != null && matchResult.groupValues[1].toInt() in start..end) {
                        reuslt = !arrayOf(
                            ScoreConsts.Attribute_F_0,
                            ScoreConsts.Attribute_F_100,
                            ScoreConsts.Attribute_F_Status,
                            ScoreConsts.Attribute_F_TotalScore
                        ).contains(score.ScoreID) && score.ScoreValue.isNotBlank()
                    }

                    return reuslt
                }

                val countA = competitorInfo.Score?.count {
                    countScored(it, 1, 10)
                } ?: 0

                val countB = competitorInfo.Score?.count {
                    countScored(it, 11, 18)
                } ?: 0

                val countC = competitorInfo.Score?.count {
                    countScored(it, 19, 23)
                } ?: 0

                val max = arrayOf(countA, countB, countC).maxByOrNull { it } ?: 0

                if (max > 0)
                    if (countB == max) //如果B表打分项多视为裁判选择了B表
                        result = StepRange(11, 18)
                    else if (countC == max) //如果C表打分项多视为裁判选择了C表
                        result = StepRange(19, 23)
            }
            //endregion

            return result
        }
        //endregion

        //region 是否在分数列表的指定区段内
        /**
         * 是否在分数列表的指定区段内
         */
        fun isInStepRange(scoreID: String, stepRange: StepRange?): Boolean {
            var result = false

            if (stepRange == null)
                result = true
            else {
                val regex = """^F_(\d+)$""".toRegex()

                val matchResult = regex.find(scoreID)
                if (matchResult != null && matchResult.groupValues[1].toInt() in stepRange.start..stepRange.end)
                    result = true
            }

            return result
        }
        //endregion
    }
}