package com.besha.egyptguide.features.profile.presenation.viewmodel

import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.Result
import com.besha.egyptguide.features.profile.data.dto.UserProfile

sealed class ProfileResults: Result<ProfileViewState> {

    data class ProfileResult(val profile: CommonViewState<UserProfile>) : ProfileResults() {
        override fun reduce(
            defaultState: ProfileViewState,
            oldState: ProfileViewState
        ): ProfileViewState {
            return oldState.copy(profile = profile)
        }
    }

    data class NotificationResult(val notification: Boolean) : ProfileResults() {
        override fun reduce(
            defaultState: ProfileViewState,
            oldState: ProfileViewState
        ): ProfileViewState {
            return oldState.copy(notification = notification)
        }

    }
}