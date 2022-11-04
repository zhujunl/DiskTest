package com.miaxis.disktest.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import com.miaxis.disktest.activity.MainActivity
import com.miaxis.disktest.utils.LogUtil

class LaunchReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || intent.action == null) return

        if (intent.action == ACTION_BOOT_COMPLETED) {
            val intentMain = Intent(context, MainActivity::class.java)
            intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(intentMain)
        }
    }
}