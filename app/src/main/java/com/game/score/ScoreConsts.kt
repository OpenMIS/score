package com.game.score

/**
 * 分数相关常量
 */
class ScoreConsts {
    companion object {
        /**
         * 特殊的XML属性 - 普通扣分：F_0
         */
        const val Attribute_F_0 = "F_0"

        /**
         * 特殊的XML属性 - 百分比扣分：F_100
         */
        const val Attribute_F_100 = "F_100"

        /**
         * 特殊的XML属性 - 总分：F_TotalScore
         */
        const val Attribute_F_TotalScore = "F_TotalScore"

        /**
         * 特殊的XML属性 - 状态：F_Status
         */
        const val Attribute_F_Status = "F_Status"

        /**
         * 分数状态 - 错误：Error
         */
        const val ScoreStatus_Error = "Error"

        /**
         * 分数状态 - 完成：Done
         */
        const val ScoreStatus_Done = "Done"

        /**
         * ScoreID="F_Status"时 - ScoreValue - 确认成绩：1
         */
        const val Status_ScoreValue_Validate = "1"

        /**
         * 错误信息 - 无效的分数
         */
        const val ScoreErrorMessage_Vaild = "Invalid score"
    }
}