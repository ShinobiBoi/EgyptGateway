package com.besha.egyptguide.features.profile.presenation.viewmodel

import com.besha.egyptguide.appcore.mvi.Action
import com.besha.egyptguide.features.profile.data.model.UpdateProfileRequest

sealed class ProfileActions : Action {
    object GetProfile : ProfileActions()

    data class UpdateProfile(val updateProfileRequest: UpdateProfileRequest) : ProfileActions()
    object LogOut : ProfileActions()
}