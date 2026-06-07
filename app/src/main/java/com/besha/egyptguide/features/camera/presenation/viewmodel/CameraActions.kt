package com.besha.egyptguide.features.camera.presenation.viewmodel

import android.net.Uri
import com.besha.egyptguide.appcore.mvi.Action
import com.besha.egyptguide.features.camera.data.dto.RatingRequest
import com.besha.egyptguide.features.camera.data.dto.VisitRequest
import okhttp3.MultipartBody

sealed class CameraActions : Action {
    data class IdentifyPhoto(val file: MultipartBody.Part) : CameraActions()

    data class Visit(val visitRequest: VisitRequest) : CameraActions()

   // object ResetVisit : CameraActions()

    data class RateMonument(val ratingRequest: RatingRequest) : CameraActions()

   // object ResetRating : CameraActions()


    data class SetSelectedImageUri(val uri: Uri) : CameraActions()

    object ResetState : CameraActions()
}
