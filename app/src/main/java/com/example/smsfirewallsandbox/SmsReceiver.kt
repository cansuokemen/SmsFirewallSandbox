package com.example.smsfirewallsandbox

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.ContentValues
import android.net.Uri
import android.provider.Telephony
import android.util.Log
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val action = intent.action
        if (action != Telephony.Sms.Intents.SMS_DELIVER_ACTION) {
            return
        }

        val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

        for (sms in smsMessages) {
            val sender = sms.displayOriginatingAddress
            val message = sms.displayMessageBody

            Log.d("SMS_FIREWALL", "Gönderen: $sender")
            Log.d("SMS_FIREWALL", "Mesaj: $message")

            Toast.makeText(
                context,
                "SMS Geldi: $message",
                Toast.LENGTH_LONG
            ).show()

            storeSmsToInbox(context, sender, message)
        }

        abortBroadcast()
    }

    private fun storeSmsToInbox(context: Context, sender: String, body: String) {
        try {
            val values = ContentValues().apply {
                put("address", sender)
                put("body", body)
                put("read", 0)
                put("date", System.currentTimeMillis())
                put("type", 1)   // 1 = gelen
            }

            val uri = Uri.parse("content://sms/inbox")
            val result = context.contentResolver.insert(uri, values)

            Log.d("SMS_FIREWALL", "SMS provider’a yazıldı: $result")
        } catch (e: Exception) {
            Log.e("SMS_FIREWALL", "SMS’i provider’a yazarken hata: ${e.message}", e)
        }
    }
}
