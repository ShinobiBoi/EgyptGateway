package com.besha.egyptguide.auth.screens.signup.domain.usecases


import com.besha.egyptguide.auth.screens.signup.data.model.SignUpForm
import com.besha.egyptguide.auth.screens.signup.data.model.SignUpRequest
import com.besha.egyptguide.auth.screens.signup.domain.repo.SignUpRepo
import javax.inject.Inject

class SignUpUseCase @Inject constructor(private val signUpRepo: SignUpRepo) {

    suspend operator fun  invoke(signUpForm: SignUpForm) =  signUpRepo.signUp(signUpForm)

}