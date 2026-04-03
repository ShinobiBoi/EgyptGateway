package com.besha.egyptguide.features.home.presenation.viewmodel

import com.besha.egyptguide.appcore.data.model.MyPlace
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.ViewState
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place

data class HomeViewState (
    val places: CommonViewState<List<MyPlace>> = CommonViewState(),
    val location: CommonViewState<LatLng> = CommonViewState(),
    val isLoading : Boolean = false
    ): ViewState
