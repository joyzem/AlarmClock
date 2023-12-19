package org.example.project.alarmclock

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    private lateinit var setAlarm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setAlarm = findViewById(R.id.alarmButton)

        setAlarm.setOnClickListener { v: View? ->
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            } else {
                val materialTimePicker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText("Выберите время для будильника")
                    .build()
                materialTimePicker.addOnPositiveButtonClickListener { view: View? ->
                    val calendar = Calendar.getInstance()
                    calendar[Calendar.SECOND] = 0
                    calendar[Calendar.MILLISECOND] = 0
                    calendar[Calendar.MINUTE] = materialTimePicker.minute
                    calendar[Calendar.HOUR_OF_DAY] = materialTimePicker.hour
                    val alarmManager =
                        getSystemService(ALARM_SERVICE) as AlarmManager
                    val alarmClockInfo =
                        AlarmClockInfo(calendar.timeInMillis, getAlarmInfoPendingIntent())
                    try {
                        alarmManager.setAlarmClock(alarmClockInfo, getAlarmActionPendingIntent())
                    } catch (e: SecurityException) {
                        Toast.makeText(this, "Security", Toast.LENGTH_SHORT).show()
                    }
                    Toast.makeText(
                        this,
                        "Будильник установлен на " + sdf.format(calendar.time),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                materialTimePicker.show(supportFragmentManager, "tag_picker")
            }
        }
    }

    private fun getAlarmInfoPendingIntent(): PendingIntent? {
        val alarmInfoIntent = Intent(this, MainActivity::class.java)
        alarmInfoIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(
            this,
            0,
            alarmInfoIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getAlarmActionPendingIntent(): PendingIntent? {
        val intent = Intent(this, AlarmActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_IMMUTABLE)
    }
}
