package com.besha.egyptguide.features.maps.presentaion.viewmodel


import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.MVIBaseViewModel
import com.besha.egyptguide.features.maps.domain.usecases.CurrentLocationUseCase
import com.besha.egyptguide.features.maps.domain.usecases.MapsNearBySearchUseCase
import com.besha.egyptguide.features.maps.domain.usecases.QueryChangeUseCase
import com.besha.egyptguide.features.maps.domain.usecases.SetPlaceUseCase
import com.besha.egyptguide.features.maps.domain.usecases.TextSearchByUseCase
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


@HiltViewModel
class MapsViewModel @Inject constructor(
    private val queryChangeUseCase: QueryChangeUseCase,
    private val setPlaceUseCase: SetPlaceUseCase,
    private val getCurrentLocationUseCase: CurrentLocationUseCase,
    private val textSearchByUseCase: TextSearchByUseCase,
    private val mapsNearBySearchUseCase: MapsNearBySearchUseCase
) : MVIBaseViewModel<MapsActions, MapsResults, MapsViewState>() {


    override val defaultViewState: MapsViewState
        get() = MapsViewState()

    override fun handleAction(action: MapsActions): Flow<MapsResults> = flow {

        when (action) {

            is MapsActions.OnQueryChange -> {
                handleQueryChange(action.newQuery, action.sessionToken, this)

            }

            is MapsActions.SelectPlace -> {

                handleSelectPlace(action.placeId, action.sessionToken, this)

            }

            is MapsActions.EmptySelectedPlace -> {
                emit(MapsResults.EmptySelectedPlace)
            }

            is MapsActions.GetCurrentLocation -> {
                try {
                    getCurrentLocationUseCase().collect { latLng ->
                        emit(MapsResults.CurrentLocation(CommonViewState(data = latLng)))

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            is MapsActions.CurrentLocationLoaded -> {
                emit(MapsResults.CurrentLocationLoaded(true))
            }


            is MapsActions.SearchByText -> {
                handleTextSearchBy(action.currentLocation, action.query, this)
            }

            is MapsActions.NearBySearch -> {
                handleNearBySearch(action.currentLocation, action.types, this)
            }

            is MapsActions.EmptyNearBySearch -> {
                emit(MapsResults.NearByPlaces(CommonViewState(data = emptyList())))
            }

            is MapsActions.ResetState -> {
                emit(
                    MapsResults.ResetState(
                        action.sessionToken,
                        action.currentLocation,
                        action.isLocationLoaded
                    )
                )
            }

        }

    }

    private suspend fun handleNearBySearch(
        currentLocation: LatLng,
        types: List<String>,
        collector: FlowCollector<MapsResults>
    ) {
        val result = mapsNearBySearchUseCase(currentLocation, types)
        collector.emit(MapsResults.NearByPlaces(CommonViewState(data = result)))

    }

    private suspend fun handleTextSearchBy(
        currentLocation: LatLng,
        query: String,
        collector: FlowCollector<MapsResults>
    ) {

        val result = textSearchByUseCase(currentLocation, query)

        collector.emit(MapsResults.NearByPlaces(CommonViewState(data = result)))


    }


    private suspend fun handleSelectPlace(
        placeId: String,
        sessionToken: AutocompleteSessionToken?,
        collector: FlowCollector<MapsResults>
    ) {

        if (sessionToken == null) {

            val newToken = AutocompleteSessionToken.newInstance()
            collector.emit(MapsResults.refreshToken(newToken))

            val place = setPlaceUseCase(placeId, newToken)

            collector.emit(MapsResults.SelectedPlace(CommonViewState(data = place)))
            collector.emit(MapsResults.refreshToken(null))

        } else {
            val place = setPlaceUseCase(placeId, sessionToken)
            collector.emit(MapsResults.SelectedPlace(CommonViewState(data = place)))
            collector.emit(MapsResults.refreshToken(null))

        }


    }

    private suspend fun handleQueryChange(
        newQuery: String,
        sessionToken: AutocompleteSessionToken?,
        collector: FlowCollector<MapsResults>
    ) {
        collector.emit(MapsResults.OnQueryChange(newQuery))

        if (sessionToken == null) {
            val newToken = AutocompleteSessionToken.newInstance()

            collector.emit(MapsResults.refreshToken(newToken))


            val predictions = queryChangeUseCase(newQuery, newToken)

            if (!predictions.isEmpty()) {
                collector.emit(MapsResults.Predictions(CommonViewState(data = predictions)))
            } else {
                collector.emit(MapsResults.Predictions(CommonViewState(data = emptyList())))
            }

        } else {
            val predictions = queryChangeUseCase(newQuery, sessionToken)

            if (!predictions.isEmpty()) {
                collector.emit(MapsResults.Predictions(CommonViewState(data = predictions)))

            } else {
                collector.emit(MapsResults.Predictions(CommonViewState(data = emptyList())))
            }
        }
    }


}