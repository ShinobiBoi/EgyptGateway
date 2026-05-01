package com.besha.egyptguide.features.profile.data.model

import android.net.Uri
import okhttp3.MultipartBody

data class UpdateProfileRequest(
    val language: String? = null,
    val name: String? = null,
    val photoUri: MultipartBody.Part? = null
)
