package com.example.smsfirewallsandbox

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val action = intent?.action

        if (action == Telephony.Sms.Intents.SMS_DELIVER_ACTION) {

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

                Toast.makeText(
                    context,
                    "SMS Geldi: $message",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
