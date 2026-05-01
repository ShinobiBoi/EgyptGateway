package com.besha.egyptguide.auth.screens.signup.domain.repo

import com.besha.egyptguide.auth.screens.signup.data.model.SignUpForm
import com.besha.egyptguide.auth.screens.signup.data.model.SignUpRequest
import com.besha.egyptguide.auth.screens.signup.data.model.SignUpResponse

interface SignUpRepo {

    suspend fun signUp(signUpForm: SignUpForm): SignUpResponse

}



