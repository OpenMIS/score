package com.game.score.models.xml.receive

import com.game.score.ScoreConsts
import com.game.score.core.IGameMessageModel
import com.game.score.core.LogUtil
import com.game.score.core.ScoreUtil
import com.game.score.models.StepRange
import javax.xml.bind.annotation.*

@XmlRootElement(name = "Body")
@XmlAccessorType(XmlAccessType.FIELD)
data class CompetitorInfoAll(
    /**
     * 消息类型
     */
    @XmlAttribute
    override val MessageType: String,

    /**
     * 场次配置代码
     */
    @XmlAttribute
    var MatchConfigCode: String,

    /**
     * 是否盛装舞步配对赛
     */
    @XmlAttribute
    var IsDRPairMatch: Boolean,

    /**
     * 【注意】可能收到空的
     */
    var CompetitorInfo: MutableList<CompetitorInfoClass>?
) : IGameMessageModel {

    /**
     * 准备数据
     */
    @XmlTransient
    fun prepare() {
        CompetitorInfo?.forEach { it.parent = this }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    data class CompetitorInfoClass(
        /**
         * 比如：盛装舞步个人赛资格赛
         */
        @XmlAttribute
        var Event: String,

        /**
         * 可能空字符串
         */
        @XmlAttribute
        var Phase: String,

        @XmlAttribute
        var CompetitorName: String,

        /**
         * 【注意】此处含字符，所以使用字符串。
         */
        @XmlAttribute
        val CompetitorID: String,

        /**
         * 裁判名称。
         *
         * 马术比如：E、M、C
         */
        @XmlAttribute
        var JudgeName: String,

        /**
         * 分数列表。
         *
         * 可能为空。
         */
        var Score: MutableList<ScoreClass>?
    ) {
        //region ScoreClass
        @XmlAccessorType(XmlAccessType.FIELD)
        data class ScoreClass(
            /**
             * 分数标识。
             *
             * 比如：F_0 .. F_n(最大大概36步)、F_TotalScore、F_Status
             *
             * F_TotalScore表示此条记录为总分
             *
             * F_Status表示validate（已经确认成绩）
             */
            @XmlAttribute
            val ScoreID: String,

            /**
             * 分数的名称。
             *
             * 比如：1 立定敬礼
             */
            @XmlAttribute
            var ScoreName: String,

            /**
             * 得分。
             *
             * 空字符串表示还未打分。
             */
            @XmlAttribute
            var ScoreValue: String,

            /**
             * 错误信息。
             *
             * 比如： 空字符串、Please Enter Score、Score out of Range
             */
            @XmlAttribute
            var ScoreErrorMessage: String,

            /**
             * 分数状态。
             *
             * 比如： 空字符串、Done、Error
             */
            @XmlAttribute
            var ScoreStatus: String
        ) {
            @XmlTransient
            fun isScoring(): Boolean = ScoreUtil.isScoring(ScoreID)

            @XmlTransient
            fun isVaild(): Boolean = ScoreUtil.isVaild(ScoreID, ScoreValue)

            companion object {
                val emptyValueInstance = ScoreClass("", "", "", "", "")
            }
        }
        //endregion

        @XmlTransient
        var parent: CompetitorInfoAll? = null

        //region 剩余必须打分的项数，已排除扣分项。
        /**
         * 剩余必须打分的项数，已排除扣分项。
         */
        @XmlTransient
        fun remainMustScoredCount(): Int {
            var result = 0

            if (this.Score != null) {
                //region 盛装舞步配对赛
                val isDRPairMatch = parent?.IsDRPairMatch ?: false //是否装舞步配对赛
                if (parent == null)
                    LogUtil.logger.error(
                        "CompetitorInfoAll的parent字段不应为空值，\r\n" +
                                Thread.currentThread().stackTrace
                    )

                val stepRange: StepRange?
                //endregion

                fun countIsBlank(score: ScoreClass) =
                    !arrayOf(
                        ScoreConsts.Attribute_F_0,
                        ScoreConsts.Attribute_F_100,
                        ScoreConsts.Attribute_F_Status,
                        ScoreConsts.Attribute_F_TotalScore
                    ).contains(score.ScoreID) && score.ScoreValue.isBlank()

                if (isDRPairMatch) {
                    stepRange = ScoreUtil.drJudgeSelectStepRange(
                        parent, this
                    )

                    result =
                        this.Score!!.count {
                            ScoreUtil.isInStepRange(
                                it.ScoreID,
                                stepRange
                            ) && countIsBlank(it)
                        }
                } else
                    result =
                        this.Score!!.count {
                            countIsBlank(it)
                        }
            }

            return result
        }
        //endregion
    }
}
