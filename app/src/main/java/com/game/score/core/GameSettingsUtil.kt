package com.game.score.core

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import com.game.score.R
import com.game.score.models.GameSettings
import kotlinx.coroutines.GlobalScope

/**
 * 设置工具
 */
class GameSettingsUtil {
    companion object {
        private var _context: ContextWrapper? = null

        //region 工具集
        private fun getSharedPreferences(context: ContextWrapper): SharedPreferences? {
            return context.getSharedPreferences(
                context.packageName + "_preferences",
                Context.MODE_PRIVATE
            )
        }
        //endregion

        fun getCurrentCompetitorInfoIndex(context: ContextWrapper): Int {
            val pref = getSharedPreferences(context)
            return pref?.getInt(
                context.getString(R.string.settings_currentCompetitorInfoIndex),
                0
            ) ?: 0
        }

        fun setCurrentCompetitorInfoIndexAsync(context: ContextWrapper, index: Int) {
            //使用异步方式保存，减少界面操作延时。
            GlobalScope.run {
                ExceptionHandlerUtil.usingExceptionHandler {
                    val pref = getSharedPreferences(context)

                    if (pref != null)
                        with(pref.edit()) {
                            putInt(
                                context.getString(R.string.settings_currentCompetitorInfoIndex),
                                index
                            )
                            commit()
                        }
                }
            }
        }

        fun getCurrentScoreIndex(context: ContextWrapper): Int {
            val pref = getSharedPreferences(context)
            return pref?.getInt(
                context.getString(R.string.settings_currentScoreIndex),
                0
            ) ?: 0
        }

        fun setCurrentScoreIndexAsync(context: ContextWrapper, index: Int) {
            //使用异步方式保存，减少界面操作延时。
            GlobalScope.run {
                ExceptionHandlerUtil.usingExceptionHandler {
                    val pref = getSharedPreferences(context)

                    if (pref != null)
                        with(pref.edit()) {
                            putInt(
                                context.getString(R.string.settings_currentScoreIndex),
                                index
                            )
                            commit()
                        }
                }
            }
        }

        fun getHaveABreak(context: ContextWrapper): Boolean {
            val pref = getSharedPreferences(context)
            return pref?.getBoolean(
                context.getString(R.string.settings_haveABreak),
                false
            ) ?: false
        }

        fun setHaveABreakAsync(context: ContextWrapper, haveABreak: Boolean) {
            //使用异步方式保存，减少界面操作延时。
            GlobalScope.run {
                ExceptionHandlerUtil.usingExceptionHandler {
                    val pref = getSharedPreferences(context)

                    if (pref != null)
                        with(pref.edit()) {
                            putBoolean(
                                context.getString(R.string.settings_haveABreak),
                                haveABreak
                            )
                            commit()
                        }
                }
            }
        }

        //region 载入设置
        /**
         * 载入设置
         */
        fun loadSettings(context: ContextWrapper) {
            _context = context
            with(context) {
                val pref = getSharedPreferences(context)

                val networkLocalPort =
                    pref?.getString(
                        context.getString(R.string.settings_network_local_port_key),
                        "8080"
                    )
                        ?.toIntOrNull() ?: 8080

                val networkServerHost =
                    pref?.getString(
                        getString(R.string.settings_network_server_host_key),
                        ""
                    ) ?: ""

                val networkServerPort =
                    pref?.getString(
                        getString(R.string.settings_network_server_port_key),
                        "8080"
                    )?.toIntOrNull() ?: 8080

                val clientId =
                    pref?.getString(getString(R.string.settings_client_id_key), "1")
                        ?.toIntOrNull()
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