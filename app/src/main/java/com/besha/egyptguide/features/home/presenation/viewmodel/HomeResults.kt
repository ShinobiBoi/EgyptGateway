package com.besha.egyptguide.features.home.presenation.viewmodel

import com.besha.egyptguide.appcore.data.model.MyPlace
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.Result
import com.besha.egyptguide.features.home.data.constants.GenreType
import com.besha.egyptguide.features.monuments.data.dto.MonumentDto
import com.google.android.gms.maps.model.LatLng

sealed class HomeResults : Result<HomeViewState>{

    data class GetPlaces(val places: CommonViewState<List<MyPlace>>) : HomeResults() {
        override fun reduce(
            defaultState: HomeViewState,
            oldState: HomeViewState
        ): HomeViewState {
            return oldState.copy(places = places)
        }
    }

    data class GetMonuments(val monuments: CommonViewState<List<MonumentDto>>) : HomeResults() {
        override fun reduce(
            defaultState: HomeViewState,
            oldState: HomeViewState
        ): HomeViewState {
            return oldState.copy(monuments = monuments)
        }
    }

    data class CurrentLocation(val location: CommonViewState<LatLng>) : HomeResults() {
        override fun reduce(
            defaultState: HomeViewState,
            oldState: HomeViewState
        ): HomeViewState {
            return oldState.copy(location = location)
        }
    }

    data class Loading(val isLoading: Boolean) : HomeResults() {
        override fun reduce(
            defaultState: HomeViewState,
            oldState: HomeViewState
        ): HomeViewState {
            return oldState.copy(isLoading = isLoading)
        }
    }

    data class SelectGenre(val genre: GenreType) : HomeResults() {
        override fun reduce(
            defaultState: HomeViewState,
            oldState: HomeViewState
        ): HomeViewState {
            return oldState.copy(selectedGenre = genre)
        }
    }
}
