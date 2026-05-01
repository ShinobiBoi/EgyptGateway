package com.besha.egyptguide.auth.screens.signup.presentation.viewmodel

import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.MVIBaseViewModel
import com.besha.egyptguide.auth.screens.signup.data.model.SignUpForm
import com.besha.egyptguide.auth.screens.signup.data.model.SignUpRequest
import com.besha.egyptguide.auth.screens.signup.domain.usecases.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
) : MVIBaseViewModel<SignUpActions, SignUpResults, SignUpViewState>() {
    override val defaultViewState: SignUpViewState
        get() = SignUpViewState()

    override fun handleAction(action: SignUpActions): Flow<SignUpResults> = flow {

        when (action) {

            is SignUpActions.SignUp -> {
                handleSignUp(this,action.signUpForm)

            }

            is SignUpActions.ResetState ->{
                handleReset(this)
            }

        }

    }

    private suspend fun handleSignUp(
        collector: FlowCollector<SignUpResults>,
        signUpForm: SignUpForm
    ) {

        collector.emit(SignUpResults.SignUp(CommonViewState(isLoading = true)))

        val response = signUpUseCase(signUpForm)


        if (response.success){
            collector.emit(SignUpResults.SignUp(CommonViewState(data = response, isSuccess = true)))

        }
        else{
            collector.emit(SignUpResults.SignUp(CommonViewState(errorThrowable = Throwable(response.message))))
        }
    }


    private suspend fun handleReset(
        collector: FlowCollector<SignUpResults>,
    ) {
        collector.emit(SignUpResults.SignUp(CommonViewState()))
    }


}

