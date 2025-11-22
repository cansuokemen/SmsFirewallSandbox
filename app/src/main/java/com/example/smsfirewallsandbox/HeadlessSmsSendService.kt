package com.example.smsfirewallsandbox

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class HeadlessSmsSendService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("SMS_FIREWALL", "📩 HeadlessSmsSendService çağrıldı (default SMS app koşulu sağlandı)")
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
