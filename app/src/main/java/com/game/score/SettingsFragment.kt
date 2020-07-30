package com.game.score

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        //region 字段
        /**
         * 是否正在显示
         */
        @JvmStatic
        var isDisplaying = false
        //endregion
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onStart() {
        super.onStart()
        isDisplaying = true
    }

    override fun onStop() {
        super.onStop()
        isDisplaying = false
    }
}