package com.besha.egyptguide.auth.screens.login.domain.repo

import android.app.Activity
import com.besha.egyptguide.auth.screens.login.data.model.GoogleSignInResult
import com.besha.egyptguide.auth.screens.login.data.model.LoginRequest
import com.besha.egyptguide.auth.screens.login.data.model.LoginResponse

interface LoginRepo {

    suspend fun LogIn(loginRequest: LoginRequest): LoginResponse


    suspend fun googleSignIn(activity: Activity): GoogleSignInResult

    suspend fun forgotPassword(email: String): LoginResponse


}