package com.example.smsfirewallsandbox

import android.Manifest
import android.app.role.RoleManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    companion object {
        private const val SMS_PERMISSION_REQUEST_CODE = 100
        private const val REQUEST_ROLE_SMS = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Varsayılan SMS uygulaması olmayı iste
        requestDefaultSmsRole()

        // SMS izinlerini kontrol et / iste
        checkSmsPermissions()
    }

    private fun requestDefaultSmsRole() {
        // Android 10+ için RoleManager ile default SMS rolünü iste
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            val isAvailable = roleManager.isRoleAvailable(RoleManager.ROLE_SMS)
            val isHeld = roleManager.isRoleHeld(RoleManager.ROLE_SMS)

            if (isAvailable && !isHeld) {
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
                startActivityForResult(intent, REQUEST_ROLE_SMS)
            }
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
            if (grantResults.isNotEmpty() &&
                grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            ) {
                Toast.makeText(this, "SMS izinleri verildi", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "SMS izinleri reddedildi", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
