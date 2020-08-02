package com.game.score.core

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import kotlin.system.exitProcess

class ExceptionHandlerUtil {
    companion object {
        //region 字段
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
                LogUtil.logger.error("使用默认异常处理", throwable)

                //由于Android会退出程序，所以需要重启APP。
                restartApp(_activity!!) //重启APP
            }
        }
        //endregion

        //region 使用异常处理
        /**
         * 使用异常处理。
         *
         * 在安卓体系的重载方法、事件（比如：点击事件等）都必须使用此方法。
         *
         * 否则当发生异常时，APP会退出。
         */
        fun usingExceptionHandler(msg: String = "", action: () -> Unit) {
            try {
                action()
            } catch (e: Throwable) {
                LogUtil.logger.error(msg, e)
            }
        }

//        /**
//         * 使用异常处理。
//         *
//         * 在安卓体系的重载方法、事件（比如：点击事件等）都必须使用此方法。
//         *
//         * 否则当发生异常时，APP会退出。
//         */
//        fun <T> usingExceptionHandlerReturn(msg: String = "", fun_: () -> T): T {
//            //此次需要使用类似C#的默认值，比如：default(T)。目前未找到对应的语法。所以先注释掉方法。
//            var result: T? = null
//            try {
//                result = fun_()
//            } catch (e: Throwable) {
//                _logger.error(msg, e)
//            }
//
//            return result!!
//        }
        //endregion
    }
}