package com.besha.egyptguide.features.home.presenation.viewmodel

import com.besha.egyptguide.appcore.mvi.Action
import com.google.android.gms.maps.model.LatLng
import okhttp3.MultipartBody

sealed class HomeActions : Action {

    data class GetCafePlaces(val currentLocation: LatLng) : HomeActions()
    data class GetHotelPlaces(val currentLocation: LatLng) : HomeActions()
    data class GetRestaurantsPlaces(val currentLocation: LatLng) : HomeActions()
    data class GetMallPlaces(val currentLocation: LatLng) : HomeActions()

    data class IdentifyPhoto(val file: MultipartBody.Part) : HomeActions()

    object GetCurrentLocation : HomeActions()


}
