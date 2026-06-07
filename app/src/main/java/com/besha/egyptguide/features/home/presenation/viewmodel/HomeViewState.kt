package com.besha.egyptguide.features.home.presenation.viewmodel

import com.besha.egyptguide.appcore.data.model.MyPlace
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.ViewState
import com.besha.egyptguide.features.home.data.constants.GenreType
import com.besha.egyptguide.features.monuments.data.dto.MonumentDto
import com.google.android.gms.maps.model.LatLng

data class HomeViewState (
    val places: CommonViewState<List<MyPlace>> = CommonViewState(),
    val monuments: CommonViewState<List<MonumentDto>> = CommonViewState(),
    val location: CommonViewState<LatLng> = CommonViewState(),
    val isLoading : Boolean = false,
    val selectedGenre: GenreType = GenreType.HOTELS
    ): ViewState
