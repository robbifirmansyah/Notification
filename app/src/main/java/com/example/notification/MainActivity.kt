package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.notification.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val channelId = "TEST_NOTIF"
    private val notifId = 90

    companion object {
        var instance: MainActivity? = null
    }

    fun updateCounters() {
        runOnUiThread {
            val sharedPref = getSharedPreferences("notifku_prefs", Context.MODE_PRIVATE)
            val countSuka = sharedPref.getInt("count_suka", 0)
            val countTidakSuka = sharedPref.getInt("count_tidak_suka", 0)

            // Tampilkan nilai pada TextView
            binding.txtCounterGanteng.text = countSuka.toString()
            binding.txtCounterJelek.text = countTidakSuka.toString()
        }
    }

    override fun onResume() {
        super.onResume()
        updateCounters()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set instance
        instance = this

        val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        binding.btnNotif.setOnClickListener {

            // Mengubah img menjadi bitmap
            val notifImage = BitmapFactory.decodeResource(resources, R.drawable.download)
            val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.hamil)

            val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE
            } else {
                0
            }

            val intentSuka = Intent(this, NotifReceiver::class.java).apply { action = "ACTION_SUKA" }
            val intentTidakSuka = Intent(this, NotifReceiver::class.java).apply { action = "ACTION_TIDAK_SUKA" }

            val pendingIntentSuka = PendingIntent.getBroadcast(this, 0, intentSuka, flag)
            val pendingIntentTidakSuka = PendingIntent.getBroadcast(this, 1, intentTidakSuka, flag)

            // Inisialisasi builder notifikasi
            val builder = NotificationCompat.Builder(this, channelId)
                .setLargeIcon(largeIcon) // Set large icon dengan Bitmap
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle("Counter")
                .setContentText("Counter Beluga Cat")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(NotificationCompat.BigPictureStyle().bigPicture(notifImage))
                .addAction(R.drawable.baseline_thumb_up_24, "Suka", pendingIntentSuka)
                .addAction(R.drawable.baseline_thumb_down_24, "Tidak Suka", pendingIntentTidakSuka)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notifChannel = NotificationChannel(channelId, "Notifku", NotificationManager.IMPORTANCE_DEFAULT)
                notifManager.createNotificationChannel(notifChannel)
            }
            notifManager.notify(notifId, builder.build())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}
