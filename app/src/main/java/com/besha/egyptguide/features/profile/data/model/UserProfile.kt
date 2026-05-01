package com.besha.egyptguide.features.profile.data.model

import android.net.Uri

data class UserProfile(
    val email: String? = null,
    val firebase_uid: String? = null,
    val gender: String? = null,
    val id: String? = null,
    val join_date: String? = null,
    val language: String? = null,
    val location: String? = null,
    val name: String? = null,
    val photo_url: String? = null,
    val points: Int? = null,
    val role: String? = null
)
