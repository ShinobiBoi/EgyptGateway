package com.besha.egyptguide.auth.screens.signup.data.repo

import com.besha.egyptguide.auth.screens.login.data.google.GoogleAuthClient
import com.besha.egyptguide.auth.screens.signup.data.model.SignUpForm
import com.besha.egyptguide.auth.screens.signup.data.model.SignUpRequest
import com.besha.egyptguide.auth.screens.signup.data.model.SignUpResponse
import com.besha.egyptguide.auth.screens.signup.domain.repo.SignUpRepo
import javax.inject.Inject

class SignUpRepoImp @Inject constructor (private val googleAuthClient: GoogleAuthClient): SignUpRepo {
    override suspend fun signUp(signUpForm: SignUpForm): SignUpResponse {
        return googleAuthClient.signUp(signUpForm)
    }
}