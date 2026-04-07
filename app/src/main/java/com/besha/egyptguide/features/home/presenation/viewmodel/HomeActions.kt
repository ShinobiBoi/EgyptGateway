package com.besha.egyptguide.features.home.presenation.viewmodel

import com.besha.egyptguide.appcore.mvi.Action
import com.besha.egyptguide.features.home.data.constants.GenreType
import com.google.android.gms.maps.model.LatLng
import okhttp3.MultipartBody

sealed class HomeActions : Action {

    data class IdentifyPhoto(val file: MultipartBody.Part) : HomeActions()

    object GetCurrentLocation : HomeActions()

    data class SelectGenre( val genre: GenreType, val location: LatLng) : HomeActions()

    object ResetIdentificationResult : HomeActions()
}
