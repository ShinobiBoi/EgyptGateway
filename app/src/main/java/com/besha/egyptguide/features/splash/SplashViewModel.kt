package com.besha.egyptguide.features.splash

import androidx.lifecycle.ViewModel
import com.besha.egyptguide.auth.authcore.domain.usecase.IsLoggedInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel@Inject constructor(
    private val isLoggedInUseCase: IsLoggedInUseCase
) : ViewModel() {

    fun isLoggedIn()= isLoggedInUseCase()
}