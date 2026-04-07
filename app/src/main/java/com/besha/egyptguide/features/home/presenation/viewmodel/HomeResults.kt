package com.besha.egyptguide.features.home.presenation.viewmodel

import com.besha.egyptguide.appcore.data.model.MyPlace
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.Result
import com.besha.egyptguide.appcore.data.remote.IdentifyResponse
import com.besha.egyptguide.features.home.data.constants.GenreType
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


    data class CurrentLocation(val location: CommonViewState<LatLng>) :HomeResults() {
        override fun reduce(
            defaultState: HomeViewState,
            oldState: HomeViewState
        ): HomeViewState {
            return oldState.copy(location = location)
        }
    }

    data class IdentifyPhoto(val identificationResult: CommonViewState<IdentifyResponse>) : HomeResults() {
        override fun reduce(
            defaultState: HomeViewState,
            oldState: HomeViewState
        ): HomeViewState {
            return oldState.copy(identificationResult = identificationResult)
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

    object ResetIdentificationResult : HomeResults() {
        override fun reduce(
            defaultState: HomeViewState,
            oldState: HomeViewState
        ): HomeViewState {
            return oldState.copy(identificationResult = CommonViewState())
        }
    }

}
