package com.game.score.core

import com.game.score.models.GameMessage

/**
 * 竞赛消息工具
 */
class GameMessageUtil {
    companion object {
        /**
         * 从原始消息转换过来
         */
        @JvmStatic
        fun convertFrom(originalMessageContent: String): GameMessage {
            val regex = """MessageType="(\w+)"""".toRegex()
            val matchResult = regex.find(originalMessageContent)
            val messageType = matchResult!!.groups[1]!!.value

            val newContent = originalMessageContent.replace(matchResult.value, "")

            return GameMessage(messageType, newContent)
        }

        /**
         * 转换到原始消息
         */
        @JvmStatic
        fun convertTo(gameMessage: GameMessage): String {
            val messageTypeString = """MessageType="${gameMessage.messageType}""""

            return gameMessage.messageContent.replace(
                """(<Body\s+)(>)""".toRegex(),
                "$1$messageTypeString$2"
            )
        }
    }
}