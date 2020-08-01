package com.game.score.core

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
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
         * @param permission The name of the permission being checked.
         *
         * @param requestCode Application specific request code to match with a result
         *    reported to {@link OnRequestPermissionsResultCallback#onRequestPermissionsResult(int, String[], int[])}.
         *    Should be >= 0.
         */
        fun requestPermissionIfNeed(
            activity: Activity, permission: String,
            requestCode: Int = 0
        ) {
            try {
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
            } catch (e: Throwable) {
                Log.e(PermissionUtil::class.java.simpleName, "initLog: ", e)
            }
        }
        //endregion

        /**
         * 请求磁盘写入权限
         */
        fun requestPermissions(activity: Activity) {
            try {
                //检测是否有写的权限
                val permission = ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // 没有写的权限，去申请写的权限，会弹出对话框
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1
                    )
                }
            } catch (e: Throwable) {
                Log.e(PermissionUtil::class.java.simpleName, "initLog: ", e)
            }
        }
    }
}

//region 扩展方法
//region 如果没有此权限，则申请此权限。
/**
 * 如果没有此权限，则申请此权限。
 *
 * @param permission The name of the permission being checked.
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