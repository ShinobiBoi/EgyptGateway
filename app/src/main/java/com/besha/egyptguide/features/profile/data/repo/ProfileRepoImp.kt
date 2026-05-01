package com.besha.egyptguide.features.profile.data.repo

import android.util.Log
import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.profile.data.model.UpdateProfileRequest
import com.besha.egyptguide.features.profile.data.model.UserProfile
import com.besha.egyptguide.features.profile.domain.remote.ProfileRemoteClient
import com.besha.egyptguide.features.profile.domain.repo.ProfileRepo
import javax.inject.Inject

class ProfileRepoImp @Inject constructor(private val profileRemoteClient: ProfileRemoteClient): ProfileRepo {
    override suspend fun getProfile():  DataState<UserProfile> {
        return profileRemoteClient.getProfile()
    }

    override suspend fun updateProfile(updateProfileRequest: UpdateProfileRequest) {
         profileRemoteClient.updateProfile(updateProfileRequest)
    }

    override suspend fun logOut() {
        profileRemoteClient.logOut()

    }
}