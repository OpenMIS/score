package com.game.score.core

import android.content.Context
import android.content.ContextWrapper
import com.game.score.R
import com.game.score.models.GameSettings

/**
 * 设置工具
 */
class GameSettingsUtil {
    companion object {
        private var _context: ContextWrapper? = null

        //region 载入设置
        /**
         * 载入设置
         */
        fun loadSettings(context: ContextWrapper) {
            _context = context
            with(context) {
                val pref = context.getSharedPreferences(
                    context.packageName + "_preferences",
                    Context.MODE_PRIVATE
                )

                val networkLocalPort =
                    pref?.getString(
                        context.getString(R.string.settings_network_local_port_key),
                        "8080"
                    )
                        ?.toIntOrNull() ?: 8080

                val networkServerHost =
                    pref?.getString(getString(R.string.settings_network_server_host_key), "") ?: ""

                val networkServerPort =
                    pref?.getString(
                        getString(R.string.settings_network_server_port_key),
                        "8080"
                    )?.toIntOrNull() ?: 8080

                val clientId =
                    pref?.getString(getString(R.string.settings_client_id_key), "1")?.toIntOrNull()
                        ?: 1

                GameSettings.last = GameSettings.current
                GameSettings.current = GameSettings(
                    networkLocalPort,
                    networkServerHost,
                    networkServerPort,
                    clientId
                )
            }
        }
        //endregion

        //region Description
        /**
         * 重新载入设置
         */
        fun reload() = loadSettings(_context!!)
        //endregion
    }
}