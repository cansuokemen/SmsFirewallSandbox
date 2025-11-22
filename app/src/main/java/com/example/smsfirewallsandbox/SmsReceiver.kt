package com.example.smsfirewallsandbox   // Eğer farklıysa MainActivity'nin package'ını yaz

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {

            val bundle = intent.extras
            val pdus = bundle?.get("pdus") as? Array<*>

            pdus?.forEach { pdu ->
                val format = bundle.getString("format")

                val sms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    SmsMessage.createFromPdu(pdu as ByteArray, format)
                } else {
                    @Suppress("DEPRECATION")
                    SmsMessage.createFromPdu(pdu as ByteArray)
                }

                val sender = sms.displayOriginatingAddress
                val message = sms.displayMessageBody

                Log.d("SMS_FIREWALL", "Gönderen: $sender")
                Log.d("SMS_FIREWALL", "Mesaj: $message")

                Toast.makeText(context, "SMS Geldi: $message", Toast.LENGTH_LONG).show()
            }
        }
    }
}
