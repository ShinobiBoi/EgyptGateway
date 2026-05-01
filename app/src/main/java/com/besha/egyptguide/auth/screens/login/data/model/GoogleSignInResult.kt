package com.besha.egyptguide.auth.screens.login.data.model

import com.besha.egyptguide.features.home.data.constants.GenreType

data class GoogleSignInResult(
    val user:UserData?,
    val errorMessage:String?
)

data class UserData(
    val userId:String?,
    val fullName :String?,
    val email:String?,
    val role:String?,
    val profilePhoto:String?
)
