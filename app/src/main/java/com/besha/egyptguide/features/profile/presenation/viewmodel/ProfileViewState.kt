package com.besha.egyptguide.features.profile.presenation.viewmodel

import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.ViewState
import com.besha.egyptguide.features.profile.data.dto.UserProfile

data class ProfileViewState (
    val profile: CommonViewState<UserProfile> = CommonViewState(),
    val notification: Boolean = false
) : ViewState


