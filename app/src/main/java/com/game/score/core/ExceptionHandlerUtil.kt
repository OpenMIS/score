package com.game.score.core

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

class ExceptionHandlerUtil {
    companion object {
        //region 字段
        private val _logger = LoggerFactory.getLogger("default")

        private var _activity: Activity? = null
        //endregion

        //region 工具集
        //region 重启APP
        /**
         * 重启APP
         */
        private fun restartApp(activity: Activity) {
            with(activity) {
                val intent = packageManager
                    .getLaunchIntentForPackage(packageName)
                val restartIntent = PendingIntent.getActivity(this, 0, intent, 0)
                val mgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                // 1秒钟后重启应用。【注意】间隔时间设置太短无法重启app
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000L, restartIntent)
            }
            exitProcess(0)
        }
        //endregion
        //endregion

        //region 设置默认的异常处理器
        /**
         * 设置默认的异常处理器
         */
        fun setDefaultUncaughtExceptionHandler(activity: Activity) {
            _activity = activity
            Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
                _logger.error("错误：", throwable)

                //由于Android会退出程序，所以需要重启APP。
                restartApp(_activity!!) //重启APP
            }
        }
        //endregion
    }
}