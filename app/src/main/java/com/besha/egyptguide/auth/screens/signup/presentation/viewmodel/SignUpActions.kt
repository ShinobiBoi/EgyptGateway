package com.besha.egyptguide.auth.screens.signup.presentation.viewmodel

import com.besha.egyptguide.appcore.mvi.Action
import com.besha.egyptguide.auth.screens.signup.data.model.SignUpForm
import com.besha.egyptguide.auth.screens.signup.data.model.SignUpRequest

sealed class SignUpActions : Action {

    data class SignUp (val signUpForm: SignUpForm) : SignUpActions()
    object ResetState : SignUpActions()



}