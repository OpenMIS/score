package com.game.score

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.game.score.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    //region 字段
    /**
     * 数据绑定
     */
    private lateinit var _binding: MainActivityBinding
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //使用数据绑定
        _binding = DataBindingUtil.setContentView(this, R.layout.main_activity)

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


//        val controller = Navigation.findNavController(window.decorView)
//        if (controller.currentDestination?.id == R.id.settingsFragment)
        //val controller = Navigation.findNavController(window.decorView)
        if (SettingsFragment.isDisplaying)

            super.onBackPressed() //取消使用“返回”键退出app
    }
}