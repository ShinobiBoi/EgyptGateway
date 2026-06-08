package com.besha.egyptguide.features.profile.presenation.viewmodel

import com.besha.egyptguide.appcore.mvi.Action
import com.besha.egyptguide.features.profile.data.dto.UpdateProfileRequest

sealed class ProfileActions : Action {
    object GetProfile : ProfileActions()

    data class UpdateProfile(val updateProfileRequest: UpdateProfileRequest) : ProfileActions()

    data class GetNotificationStatus (val context: android.content.Context): ProfileActions()
    data class ToggleNotification(val notification: Boolean) : ProfileActions()
    data class ScheduleFoodAlarms(val category: String, val time: java.time.LocalTime) : ProfileActions()
    object LogOut : ProfileActions()
}