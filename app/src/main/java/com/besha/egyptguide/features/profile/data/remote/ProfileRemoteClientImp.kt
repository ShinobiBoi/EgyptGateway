package com.besha.egyptguide.features.profile.data.remote

import android.provider.ContactsContract
import android.util.Log
import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.data.remote.BackEndServices
import com.besha.egyptguide.features.profile.data.model.UpdateProfileRequest
import com.besha.egyptguide.features.profile.data.model.UserProfile
import com.besha.egyptguide.features.profile.domain.remote.ProfileRemoteClient
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class ProfileRemoteClientImp @Inject constructor(
    private val backEndServices: BackEndServices
) : ProfileRemoteClient {

    private val auth = Firebase.auth



    override suspend fun getProfile(): DataState<UserProfile> {
        val result = backEndServices.getUser()

        if (result.isSuccessful) {
            return DataState.Success(result.body()!!)
        }
        else
            return DataState.Error(Throwable(result.message()))

    }

    override suspend fun updateProfile(updateProfileRequest: UpdateProfileRequest) {
        Log.d("TAG",  updateProfileRequest.toString())
        backEndServices.updateProfile(
            name = updateProfileRequest.name?.toRequestBody("text/plain".toMediaTypeOrNull()),
            language = updateProfileRequest.language?.toRequestBody("text/plain".toMediaTypeOrNull()),
            photo = updateProfileRequest.photoUri,
            location = null,
            gender = null
        )
    }

    override suspend fun logOut() {
        auth.signOut()
    }


}