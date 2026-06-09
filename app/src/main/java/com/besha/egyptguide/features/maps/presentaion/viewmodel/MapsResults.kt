package com.besha.egyptguide.features.maps.presentaion.viewmodel

import com.besha.egyptguide.appcore.data.model.MyPlace
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.Result
import com.besha.egyptguide.features.maps.data.dto.RoutesDto
import com.besha.egyptguide.features.maps.domain.model.MapsPlace
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken

sealed class MapsResults() : Result<MapsViewState> {

    data class OnQueryChange(val newQuery: String) : MapsResults() {
        override fun reduce(
            defaultState: MapsViewState,
            oldState: MapsViewState
        ): MapsViewState {
            return oldState.copy(query = newQuery)
        }
    }

    data class Predictions(val predictions: CommonViewState<List<AutocompletePrediction>>) :
        MapsResults() {
        override fun reduce(
            defaultState: MapsViewState,
            oldState: MapsViewState
        ): MapsViewState {
            return oldState.copy(predictions = predictions)
        }
    }

    data class refreshToken(val newToken: AutocompleteSessionToken?) : MapsResults() {
        override fun reduce(
            defaultState: MapsViewState,
            oldState: MapsViewState
        ): MapsViewState {
            return oldState.copy(sessionToken = newToken)
        }
    }

    data class SelectedPlace(val place: CommonViewState<MyPlace>) : MapsResults() {
        override fun reduce(
            defaultState: MapsViewState,
            oldState: MapsViewState
        ): MapsViewState {
            return oldState.copy(selectedPlace = place)
        }
    }

    object EmptySelectedPlace : MapsResults() {
        override fun reduce(
            defaultState: MapsViewState,
            oldState: MapsViewState
        ): MapsViewState {
            return oldState.copy(selectedPlace = CommonViewState())
        }
    }

    data class NearByPlaces(val places: CommonViewState<List<MapsPlace>>) : MapsResults() {
        override fun reduce(
            defaultState: MapsViewState,
            oldState: MapsViewState
        ): MapsViewState {
            return oldState.copy(nearByPlaces = places)
        }
    }

    data class CurrentLocation(val location: CommonViewState<LatLng>) : MapsResults() {
        override fun reduce(
            defaultState: MapsViewState,
            oldState: MapsViewState
        ): MapsViewState {
            return oldState.copy(currentLocation = location)
        }
    }

    data class CurrentLocationLoaded(val isLoaded: Boolean) : MapsResults() {
        override fun reduce(
            defaultState: MapsViewState,
            oldState: MapsViewState
        ): MapsViewState {
            return oldState.copy(isCurrentlocationLoaded = isLoaded)
        }

    }

    data class ResetState(val sessionToken: AutocompleteSessionToken?,val currentLocation: LatLng, val isLocationLoaded: Boolean) :
        MapsResults() {
        override fun reduce(
            defaultState: MapsViewState,
            oldState: MapsViewState
        ): MapsViewState {
            return defaultState.copy(
                sessionToken = sessionToken,
                currentLocation = CommonViewState(data = currentLocation),
                isCurrentlocationLoaded = isLocationLoaded
            )
        }
    }

    data class Routes(val routes: CommonViewState<RoutesDto>) : MapsResults() {
        override fun reduce(
            defaultState: MapsViewState,
            oldState: MapsViewState
        ): MapsViewState {
            return oldState.copy(routes = routes)
        }
    }


}
