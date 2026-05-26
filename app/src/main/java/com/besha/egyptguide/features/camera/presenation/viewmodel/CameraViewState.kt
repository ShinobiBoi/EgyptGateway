package com.besha.egyptguide.features.camera.presenation.viewmodel

import android.net.Uri
import com.besha.egyptguide.appcore.data.remote.IdentifyResponse
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.ViewState
import com.besha.egyptguide.features.camera.data.model.RatingResponse
import com.besha.egyptguide.features.camera.data.model.VisitResponse

data class CameraViewState(
    val identificationResult: CommonViewState<IdentifyResponse> = CommonViewState(),
    val visitResponse: CommonViewState<VisitResponse> = CommonViewState(),
    val rateResponse: CommonViewState<RatingResponse> = CommonViewState(),
    val selectedImageUri: Uri? = null
    ) : ViewState
