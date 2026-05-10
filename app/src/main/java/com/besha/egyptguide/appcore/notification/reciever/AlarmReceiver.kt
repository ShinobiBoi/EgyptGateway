package com.besha.egyptguide.appcore.notification.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.besha.egyptguide.appcore.notification.alarm.AlarmItem
import com.besha.egyptguide.appcore.notification.alarm.AndroidAlarmSchedule
import com.besha.egyptguide.appcore.notification.worker.FoodAlarmWorker
import java.time.LocalDateTime

class AlarmReceiver : BroadcastReceiver()  {
    override fun onReceive(context: Context, intent: Intent?) {

        val title = intent?.getStringExtra("EXTRA_TITLE") ?: return
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: return
        val category = intent.getStringExtra("EXTRA_CATEGORY") ?: return

        val workRequest = OneTimeWorkRequestBuilder<FoodAlarmWorker>()
            .setInputData(
                workDataOf(
                    "message" to message,
                    "title" to title,
                    "category" to category
                )
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        val nextAlarm = AlarmItem(
            time = LocalDateTime.now().plusDays(1),
            foodCategory = category,
            message = message,
            title = title
        )

        AndroidAlarmSchedule(context).schedule(nextAlarm)

        WorkManager
            .getInstance(context)
            .enqueue(workRequest)
    }
}