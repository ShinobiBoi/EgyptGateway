package com.besha.egyptguide.appcore.notification.alarm

interface AlarmScheduler {
    fun schedule(item: AlarmItem)

    fun cancel(item: AlarmItem)
}