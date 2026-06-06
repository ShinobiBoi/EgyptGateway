package com.besha.egyptguide.features.profile.data.dto

import okhttp3.MultipartBody

data class UpdateProfileRequest(
    val language: String? = null,
    val name: String? = null,
    val photoUri: MultipartBody.Part? = null
)
