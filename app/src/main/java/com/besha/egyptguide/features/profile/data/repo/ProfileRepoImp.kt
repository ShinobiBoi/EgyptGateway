package com.besha.egyptguide.features.profile.data.repo

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.profile.data.dto.UpdateProfileRequest
import com.besha.egyptguide.features.profile.data.dto.UserProfile
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