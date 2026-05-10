package com.besha.egyptguide.appcore.notification.alarm

import java.time.LocalDateTime

data class AlarmItem(
    val time : LocalDateTime,
    val foodCategory : String,
    val title : String,
    val message : String,
)

