package com.game.score.core

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

/**
 * 权限工具
 */
class PermissionUtil {

    companion object {
        //region 如果没有此权限，则申请此权限。
        /**
         * 如果没有此权限，则申请此权限。
         *
         * @param permission 使用[android.Manifest.permission]类里定义的字符串常量。
         *
         * @param requestCode Application specific request code to match with a result
         *    reported to {@link OnRequestPermissionsResultCallback#onRequestPermissionsResult(int, String[], int[])}.
         *    Should be >= 0.
         */
        fun requestPermissionIfNeed(
            activity: Activity, permission: String,
            requestCode: Int = 0
        ) {
            ExceptionHandlerUtil.usingExceptionHandler {
                //检测是否有写的权限
                val permissionInt = ActivityCompat.checkSelfPermission(
                    activity, permission
                )

                if (permissionInt != PackageManager.PERMISSION_GRANTED) {
                    // 没有写的权限，去申请写的权限，会弹出对话框
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(permission),
                        requestCode
                    )
                }
            }
        }
        //endregion
    }
}

//region 扩展方法
//region 如果没有此权限，则申请此权限。
/**
 * 如果没有此权限，则申请此权限。
 *
 * @param permission 使用[android.Manifest.permission]类里定义的字符串常量。
 *
 * @param requestCode Application specific request code to match with a result
 *    reported to {@link OnRequestPermissionsResultCallback#onRequestPermissionsResult(int, String[], int[])}.
 *    Should be >= 0.
 */
fun Activity.requestPermissionIfNeed(
    permission: String,
    requestCode: Int = 0
) = PermissionUtil.requestPermissionIfNeed(this, permission, requestCode)
//endregion
//endregion