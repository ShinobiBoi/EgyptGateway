package com.besha.egyptguide.features.camera.presenation.viewmodel

import android.net.Uri
import com.besha.egyptguide.appcore.data.remote.IdentifyResponse
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.Result
import com.besha.egyptguide.features.camera.data.model.RatingResponse
import com.besha.egyptguide.features.camera.data.model.VisitResponse

sealed class CameraResults : Result<CameraViewState> {
    data class IdentifyResult(val result: CommonViewState<IdentifyResponse>) : CameraResults() {
        override fun reduce(defaultState: CameraViewState, oldState: CameraViewState): CameraViewState {
            return oldState.copy(identificationResult = result)
        }
    }

    data class Visit(val visitResponse: CommonViewState<VisitResponse>) : CameraResults() {
        override fun reduce(
            defaultState: CameraViewState,
            oldState: CameraViewState
        ): CameraViewState {
            return oldState.copy(visitResponse = visitResponse)
        }
    }

    data class RateResult(val result: CommonViewState<RatingResponse>) : CameraResults() {
        override fun reduce(defaultState: CameraViewState, oldState: CameraViewState): CameraViewState {
            return oldState.copy(rateResponse = result)
        }
    }

    data class SetSelectedImageUri(val uri: Uri) : CameraResults(){
        override fun reduce(defaultState: CameraViewState, oldState: CameraViewState): CameraViewState {
            return oldState.copy(selectedImageUri =  uri)
        }
    }

    object ResetStateResult : CameraResults() {
        override fun reduce(defaultState: CameraViewState, oldState: CameraViewState): CameraViewState {
            return defaultState
        }
    }
}
