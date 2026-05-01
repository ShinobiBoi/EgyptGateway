package com.besha.egyptguide.auth.screens.signup.data.model

data class SignUpRequest(
    val name: String,
    val email: String,
    val photo_url: String,
    val language: String?,
)
