package com.game.score.core

import android.view.Gravity
import android.widget.Toast

object ToastHelper {
    fun setPosition(toast: Toast): Toast =
        toast.apply { setGravity(Gravity.CENTER, 0, -280) }
}

//region 扩展方法
/**
 * 设置显示位置。
 *
 */
fun Toast.setPosition(): Toast =
    ToastHelper.setPosition(this)
//endregion