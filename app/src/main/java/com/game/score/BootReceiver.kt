package com.game.score

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {        // boot
            val intent2 = Intent(context, MainActivity::class.java)
            //			intent2.setAction("android.intent.action.MAIN");
//			intent2.addCategory("android.intent.category.LAUNCHER");
            intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent2)
        }
    }
}