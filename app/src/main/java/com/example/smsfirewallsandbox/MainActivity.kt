package com.example.smsfirewallsandbox

import android.Manifest
import android.app.role.RoleManager
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    companion object {
        private const val SMS_PERMISSION_REQUEST_CODE = 100
        private const val REQUEST_ROLE_SMS = 200
    }

    private lateinit var recyclerView: RecyclerView

    // SMS veritabanındaki değişiklikleri izleyecek gözlemci
    private var smsObserver: ContentObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerSms)
        recyclerView.layoutManager = LinearLayoutManager(this)

        requestDefaultSmsRole()
        checkSmsPermissions()
    }

    override fun onStart() {
        super.onStart()
        // SMS tablosundaki değişiklikleri dinle
        if (smsObserver == null) {
            smsObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    super.onChange(selfChange)
                    // Her değişiklikte listeyi yenile
                    loadSms()
                }
            }

            contentResolver.registerContentObserver(
                Uri.parse("content://sms"),
                true,          // alt path'leri de dinle
                smsObserver!!
            )
        }
    }

    override fun onStop() {
        super.onStop()
        // Observer’ı kaldır
        smsObserver?.let {
            try {
                contentResolver.unregisterContentObserver(it)
            } catch (_: Exception) {
            }
        }
        smsObserver = null
    }

    private fun requestDefaultSmsRole() {
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
            loadSms()
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
                loadSms()
            } else {
                Toast.makeText(this, "SMS izinleri reddedildi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadSms() {
        try {
            val smsList = mutableListOf<SmsModel>()

            val uri = Uri.parse("content://sms/inbox")
            val projection = arrayOf("address", "body", "date")

            val cursor = contentResolver.query(
                uri,
                projection,
                null,
                null,
                "date DESC"
            )

            cursor?.use {
                val idxAddress = it.getColumnIndexOrThrow("address")
                val idxBody = it.getColumnIndexOrThrow("body")
                val idxDate = it.getColumnIndexOrThrow("date")

                while (it.moveToNext()) {
                    val address = it.getString(idxAddress) ?: ""
                    val body = it.getString(idxBody) ?: ""
                    val date = it.getLong(idxDate)

                    smsList.add(SmsModel(address, body, date))
                }
            }

            recyclerView.adapter = SmsAdapter(smsList)

        } catch (e: SecurityException) {
            Toast.makeText(this, "SMS okuma izni yok", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("SMS_FIREWALL", "SMS yüklenirken hata", e)
        }
    }
}
