package com.example.smsfirewallsandbox

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val SMS_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Varsayılan SMS uygulaması olmayı iste
        requestDefaultSmsRole()

        // SMS izinlerini kontrol et / iste
        checkSmsPermissions()
    }


    private fun requestDefaultSmsRole() {
        val currentDefault = Telephony.Sms.getDefaultSmsPackage(this)
        if (currentDefault != packageName) {
            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT).apply {
                putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
            }
            startActivity(intent)
        }
    }

    private fun checkSmsPermissions() {
        val receiveGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED

        val readGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED

        if (!receiveGranted || !readGranted) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS
                ),
                SMS_PERMISSION_REQUEST_CODE
            )
        } else {
            Toast.makeText(this, "SMS izinleri zaten verilmiş", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "SMS izinleri verildi", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "SMS izinleri reddedildi", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
