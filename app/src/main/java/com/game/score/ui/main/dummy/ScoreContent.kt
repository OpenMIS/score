package com.game.score.ui.main.dummy

import com.game.score.models.xml.receive.CompetitorInfo
import java.util.*

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
object ScoreContent {

    /**
     * 分数模型 列表（测试数据）
     */
    var competitorInfo: CompetitorInfo

    /**
     * 分数模型 列表（测试数据）
     */
    private val _scores: MutableList<CompetitorInfo.CompetitorInfoClass.Score> = ArrayList()

    init {
        //region 分数列表
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_1",
                "1 立定敬礼",
                "6",
                "",
                ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_2",
                "2 快步 ",
                "6.1",
                "",
                ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_3",
                "3 园（左）",
                "",
                "",
                ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_4",
                "4 斜横步（左） (2)",
                "11",
                "Score out of Range",
                "Error"
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_5", "5 快步 ", "7", "", ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_6", "6 过渡 ", "5.1", "", ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_7",
                "7 肩向内（右）",
                "8",
                "",
                ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_8",
                "8 伸长跑步",
                "7.8",
                "",
                ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_9",
                "9 空中变脚",
                "8.6",
                "",
                ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_10", "10 步法 ", "8.9", "", ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_11", "11 推进 ", "8.5", "", ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_12",
                "12 顺从(2)",
                "8.3",
                "",
                ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_13",
                "13 骑坐(2)",
                "8.2",
                "",
                ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_14", "14 过渡 ", "9.2", "", ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_15", "15 快步 ", "8", "", ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_16",
                "16 伸长跑步",
                "9.4",
                "",
                ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_17",
                "17 园（左）",
                "6.3",
                "",
                ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_18",
                "17 后肢旋转（左） (2)",
                "9.2",
                "",
                ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_19",
                "19 空中变脚",
                "",
                "",
                ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_20",
                "20 后肢旋转（右） (2)",
                "9.1",
                "",
                ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_21",
                "21 空中变脚",
                "9.3",
                "",
                ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_22",
                "22 7个2步一换",
                "9.4",
                "",
                ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_23",
                "23 伸长快步",
                "",
                "Please Enter Score",
                "Error"
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_24",
                "24 过渡",
                "",
                "",
                ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_25",
                "25 立定敬礼",
                "",
                "",
                ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_Status",
                "",
                "",
                "",
                ""
            )
        )
        _scores.add(
            CompetitorInfo.CompetitorInfoClass.Score(
                "F_TotalScore",
                "TotalScore",
                "",
                "",
                ""
            )
        )
        //endregion

        competitorInfo = CompetitorInfo(
            "CompetitorInfo",
            CompetitorInfo.CompetitorInfoClass(
                Event = "盛装舞步个人赛资格赛",
                Phase = "",
                CompetitorName = "贾海涛(浙江队)",

                /**
                 * 【注意】此处含字符，所以使用字符串。
                 */
                CompetitorID = "1",

                /**
                 * 裁判名称。
                 *
                 * 马术比如：E、M、C
                 */
                JudgeName = "E",

                Scores = _scores
            )
        )
    }
}