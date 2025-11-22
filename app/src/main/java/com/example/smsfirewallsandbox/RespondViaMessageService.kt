package com.example.smsfirewallsandbox

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class RespondViaMessageService : Service() {

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("SMS_FIREWALL", "RespondViaMessageService called")

        stopSelf()
        return START_NOT_STICKY
    }
}
