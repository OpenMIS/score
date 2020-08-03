package com.game.score

/**
 * 分数相关常量
 */
class ScoreConsts {
    companion object {
        /**
         * 特殊的XML属性 - 扣分：F_0
         */
        const val Attribute_F_0: String = "F_0"

        /**
         * 特殊的XML属性 - 总分：F_TotalScore
         */
        const val Attribute_F_TotalScore: String = "F_TotalScore"

        /**
         * 特殊的XML属性 - 状态：F_Status
         */
        const val Attribute_F_Status: String = "F_Status"

        /**
         * 分数状态 - 错误：Error
         */
        const val ScoreStatus_Error: String = "Error"

        /**
         * 分数状态 - 完成：Done
         */
        const val ScoreStatus_Done: String = "Done"

        /**
         * ScoreID="F_Status"时 - ScoreValue - 确认成绩：1
         */
        const val Status_ScoreValue_Validate: String = "1"
    }
}