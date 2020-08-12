package com.game.score

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    private lateinit var _mainViewModel: MainViewModel

    //Looper.getMainLooper()
    inner class AppOfflineStatusHandler(
        private val _mainActivity: MainActivity,
        private val _mainViewModel: MainViewModel
    ) : Handler() {
        /*
     * handleMessage() defines the operations to perform when
     * the Handler receives a new Message to process.
     */
        override fun handleMessage(inputMessage: Message) {//在界面线程处理
            ExceptionHandlerUtil.usingExceptionHandler {
                with(_mainActivity.findViewById<TextView>(R.id.textView_appStatus)) {
                    val aa = _mainActivity.getString(R.string.app_status_offline)
                    _mainViewModel.appStatus.value = aa
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorAppStatus_Offline
                        )
                    )
                }
            }
        }
    }

    lateinit var handleAppOfflineStatus: Handler

//region 与Android 本地服务对接 的字段
    /**
     * Android本地服务
     */
    private var _gameService: GameService? = null

    /**
     * 是否已经绑定到Android本地服务
     */
    private var _isBoundService = false

    /**
     * Android本地服务连接
     */
    private val _serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            ExceptionHandlerUtil.usingExceptionHandler {
                val gameBinder: GameService.GameBinder = service as GameService.GameBinder
                _gameService = gameBinder.service
                _isBoundService = true

                MessageDistribute.registerGameMessageHandlerIfNeed(GameMessageHandler)
                //把消息分发器传给Android本地服务
                _gameService?.setGameMessageHandler(MessageDistribute.instance)
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            _isBoundService = false
        }
    }
//endregion
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
            _mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
            //region MainViewModel默认值
            if (_mainViewModel.competitorName.value.isNullOrBlank()) {
                _mainViewModel.competitorName.value =
                    getString(R.string.validate_success_competitorName)
                _mainViewModel.competitorName_Normal.value = false
            }

            if (_mainViewModel.appStatus.value.isNullOrBlank()) {
                _mainViewModel.appStatus.value = getString(R.string.app_status_offline)
            }
            //endregion

            GameMessageHandler.init(this, _mainViewModel)

            supportActionBar?.hide() //隐藏头部动作栏

            //载入设置
            GameSettingsUtil.loadSettings(this)

            GameUdp.initForUI(this)

            //绑定到Android本地服务
            val intent = Intent(this, GameService::class.java)
            bindService(intent, _serviceConnection, Context.BIND_AUTO_CREATE)

            handleAppOfflineStatus = AppOfflineStatusHandler(this, _mainViewModel)
        }
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are *not* resumed.
     */
    override fun onResume() {
        super.onResume()

        ExceptionHandlerUtil.usingExceptionHandler {
            _mainViewModel.currentScoreIndex.value.let { index ->
                if (index != null && index >= 0 &&
                    _mainViewModel.competitorInfo.value?.CompetitorInfo?.Score != null &&
                    index < _mainViewModel.competitorInfo.value!!.CompetitorInfo.Score!!.count()
                ) {
                    val recyclerView = findViewById<RecyclerView>(R.id.score_list)
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.scrollToPositionWithOffset(
                        index,
                        /*距离顶部的像素。通过此值，让正在打分的项尽量列表的上下的中间位置，
                        这样方便看到之前打分与之后要打的分。
                        */
                        100
                    )
                }
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        ExceptionHandlerUtil.usingExceptionHandler {
            if (_isBoundService) {
                unbindService(_serviceConnection)
                _isBoundService = false
            }
        }
    }
//endregion
}