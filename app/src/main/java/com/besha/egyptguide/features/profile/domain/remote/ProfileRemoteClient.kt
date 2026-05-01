package com.besha.egyptguide.features.profile.domain.remote

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.profile.data.model.UpdateProfileRequest
import com.besha.egyptguide.features.profile.data.model.UserProfile

interface ProfileRemoteClient {

    suspend fun getProfile(): DataState<UserProfile>

    suspend fun updateProfile(updateProfileRequest: UpdateProfileRequest)


    suspend fun logOut()
}