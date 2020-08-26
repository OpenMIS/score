package com.game.score.core

/**
 * 竞赛消息工具
 */
class GameMessageUtil {
    companion object {
        /**
         * 获取消息类型
         */
        @JvmStatic
        fun getMessageType(originalMessageContent: String): String {
            val regex = """MessageType="(\w+)"""".toRegex()

            val matchResult = regex.find(originalMessageContent)
            return matchResult!!.groups[1]!!.value
        }
    }
}