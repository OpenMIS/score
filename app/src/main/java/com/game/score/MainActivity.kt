package com.game.score

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.game.score.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }

        supportActionBar?.hide() //隐藏头部动作栏
    }
}