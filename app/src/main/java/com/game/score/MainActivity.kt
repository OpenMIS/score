package com.game.score

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.game.score.core.*
import com.game.score.databinding.ActivityMainBinding
import com.game.score.models.GameSettings
import com.game.score.ui.main.MainViewModel


class MainActivity : AppCompatActivity() {

    //region 字段
    /**
     * 数据绑定
     */
    private lateinit var _binding: ActivityMainBinding
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ExceptionHandlerUtil.usingExceptionHandler {

            //请求写入内部 与 外部 SD卡。写入日志需要此权限。
            this.requestPermissionIfNeed(WRITE_EXTERNAL_STORAGE)

            ExceptionHandlerUtil.setDefaultUncaughtExceptionHandler(this) //设置默认的异常处理器

            //使用数据绑定
            _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //创建视图模型
        ViewModelProvider(this)[MainViewModel::class.java]

        supportActionBar?.hide() //隐藏头部动作栏
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key. The [OnBackPressedDispatcher][.getOnBackPressedDispatcher] will be given a
     * chance to handle the back button before the default behavior of
     * [android.app.Activity.onBackPressed] is invoked.
     *
     * @see .getOnBackPressedDispatcher
     */
    override fun onBackPressed() {
        ExceptionHandlerUtil.usingExceptionHandler {
            if (SettingsFragment.isDisplaying) {
                super.onBackPressed() //仅“设置”页面可以使用“返回”键，主页面不能使用“返回”退出app。

                GameSettingsUtil.reload() //重新载入设置

                if (GameSettings.isChangeSettingsForReceive())
                    _gameService?.restartReceiveThread()
            }
        }
    }
}