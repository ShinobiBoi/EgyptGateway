package com.besha.egyptguide.auth.screens.login.domain.usecases

import com.besha.egyptguide.auth.screens.login.data.model.LoginResponse
import com.besha.egyptguide.auth.screens.login.domain.repo.LoginRepo
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(private val loginRepo: LoginRepo) {
    suspend operator fun invoke(email: String): LoginResponse {
        return loginRepo.forgotPassword(email)
    }
}
