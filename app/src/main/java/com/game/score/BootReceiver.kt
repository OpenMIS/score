package com.game.score

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {        // boot
            val intent2 = Intent(context, MainActivity::class.java)
            intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent2)
        }
    }
}