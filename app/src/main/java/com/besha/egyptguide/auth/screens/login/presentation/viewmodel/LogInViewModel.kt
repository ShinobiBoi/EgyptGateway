package com.besha.egyptguide.auth.screens.login.presentation.viewmodel

import android.app.Activity
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.MVIBaseViewModel
import com.besha.egyptguide.auth.screens.login.data.model.LoginRequest
import com.besha.egyptguide.auth.screens.login.domain.usecases.ForgotPasswordUseCase
import com.besha.egyptguide.auth.screens.login.domain.usecases.GoogleSignInUseCase
import com.besha.egyptguide.auth.screens.login.domain.usecases.LogInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


@HiltViewModel
class LogInViewModel @Inject constructor(
    private val logInUseCase: LogInUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : MVIBaseViewModel<LogInActions, LogInResults, LogInViewState>() {

    override val defaultViewState: LogInViewState
        get() = LogInViewState()

    override fun handleAction(action: LogInActions): Flow<LogInResults> = flow {

        when (action) {

            is LogInActions.LogIn -> {
                handleLogIn(this, action.loginRequest)
            }


            is LogInActions.ResetState -> {

                handleReset(this)

            }

            is LogInActions.GoogleSignIn -> {
                handleGoogleSignInResult(this,action.activity)
            }

            is LogInActions.ForgotPassword -> {
                handleForgotPassword(this, action.email)
            }

        }

    }

    private suspend fun handleGoogleSignInResult(
        collector: FlowCollector<LogInResults>,
        activity: Activity

    ) {
        collector.emit(LogInResults.LogIn(CommonViewState(isLoading = true)))

        val result = googleSignInUseCase(activity)

        if (result.user != null) {

            collector.emit(LogInResults.LogIn(CommonViewState(isSuccess = true)))

        } else {
            collector.emit(LogInResults.LogIn(CommonViewState(errorThrowable = Throwable(result.errorMessage ?: "Google sign-in failed"))))
        }

    }



    private suspend fun handleLogIn(
        collector: FlowCollector<LogInResults>,
        loginRequest: LoginRequest
    ) {
        collector.emit(LogInResults.LogIn(CommonViewState(isLoading = true)))

        val result = logInUseCase(loginRequest)

        if (result.isSuccessful) {
            collector.emit(LogInResults.LogIn(CommonViewState(isSuccess = true)))

        } else {
            collector.emit(LogInResults.LogIn(CommonViewState(errorThrowable = Throwable(result.errorMessage))))
        }

    }

    private suspend fun handleForgotPassword(
        collector: FlowCollector<LogInResults>,
        email: String
    ) {
        collector.emit(LogInResults.LogIn(CommonViewState(isLoading = true)))

        val result = forgotPasswordUseCase(email)

        if (result.isSuccessful) {
            collector.emit(LogInResults.LogIn(CommonViewState(isSuccess = true)))

        } else {
            collector.emit(LogInResults.LogIn(CommonViewState(errorThrowable = Throwable(result.errorMessage))))
        }
    }

    private suspend fun handleReset(
        collector: FlowCollector<LogInResults>,
    ) {
        collector.emit(LogInResults.LogIn(CommonViewState()))
    }


}