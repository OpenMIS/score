package com.game.score.models

data class GameSettings(
    /**
     * 网络 - 本地端口
     */
    val network_local_port: Int,

    /**
     * 网络 - 服务器IP
     */
    val network_server_host: String,

    /**
     * 网络 - 服务器端口
     */
    val network_server_port: Int,

    /**
     * 客户端ID: 1-3
     */
    val client_id: Int
) {
    companion object {
        /**
         * 上次设置
         */
        var last: GameSettings? = null

        /**
         * 当前设置
         */
        var current: GameSettings? = null

        //region 是否修改与接收消息用到的设置
        /**
         * 是否修改与接收消息用到的设置
         *
         * 比如：服务端IP、本地端口
         */
        fun isChangeSettingsForReceive(): Boolean {
            var result = false
            if (last == null && current != null)
                result = true
            else if (last != null && current != null) {
                if (last!!.network_server_host != current!!.network_server_host ||
                    last!!.network_local_port != current!!.network_local_port
                )
                    result = true
            }

            return result
        }
        //endregion

        //region 是否修改与发送消息用到的设置
        /**
         * 是否修改与发送消息用到的设置
         *
         * 比如：服务端IP、服务端端口
         */
        fun isChangeSettingsForSend(): Boolean {
            var result = false
            if (last == null && current != null)
                result = true
            else if (last != null && current != null) {
                if (last!!.network_server_host != current!!.network_server_host ||
                    last!!.network_server_port != current!!.network_server_port
                )
                    result = true
            }

            return result
        }
        //endregion
    }
}