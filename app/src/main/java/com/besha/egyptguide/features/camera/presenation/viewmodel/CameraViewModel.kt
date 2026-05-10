package com.besha.egyptguide.features.camera.presenation.viewmodel

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.MVIBaseViewModel
import com.besha.egyptguide.features.camera.domain.usecase.IdentifyPhotoUseCase
import com.besha.egyptguide.features.camera.domain.usecase.RateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val identifyPhotoUseCase: IdentifyPhotoUseCase,
    private val rateUseCase: RateUseCase
) : MVIBaseViewModel<CameraActions, CameraResults, CameraViewState>() {

    override val defaultViewState: CameraViewState
        get() = CameraViewState()

    override fun handleAction(action: CameraActions): Flow<CameraResults> = flow {
        when (action) {
            is CameraActions.IdentifyPhoto -> {
                emit(CameraResults.IdentifyResult(CommonViewState(isLoading = true)))

                delay(5000)
                val result = identifyPhotoUseCase(action.file)
                when (result) {
                    is DataState.Success -> {
                        emit(CameraResults.IdentifyResult(CommonViewState(data = result.data)))
                    }
                    is DataState.Error -> {
                        emit(CameraResults.IdentifyResult(CommonViewState(errorThrowable = result.throwable)))
                    }
                    else -> {}
                }
            }
            is CameraActions.RateMonument -> {
                emit(CameraResults.RateResult(CommonViewState(isLoading = true)))
                val result = rateUseCase(action.ratingRequest)
                when (result) {
                    is DataState.Success -> {
                        emit(CameraResults.RateResult(CommonViewState(data = result.data)))
                    }
                    is DataState.Error -> {
                        emit(CameraResults.RateResult(CommonViewState(errorThrowable = result.throwable)))
                    }
                    else -> {}
                }
            }

            is CameraActions.SetSelectedImageUri->{
                emit(CameraResults.SetSelectedImageUri(action.uri))
            }
            is CameraActions.ResetState -> {
                emit(CameraResults.ResetStateResult)
            }
        }
    }
}
