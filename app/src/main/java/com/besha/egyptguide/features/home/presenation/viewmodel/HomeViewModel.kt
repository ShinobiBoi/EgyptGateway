package com.besha.egyptguide.features.home.presenation.viewmodel

import com.besha.egyptguide.appcore.data.remote.BackEndServices
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.MVIBaseViewModel
import com.besha.egyptguide.features.home.domain.usecase.NearBySearchUseCase
import com.besha.egyptguide.features.maps.domain.usecases.CurrentLocationUseCase
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.PlaceTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val backEndServices: BackEndServices,
    private val nearBySearchUseCase: NearBySearchUseCase,
    private val getCurrentLocationUseCase: CurrentLocationUseCase,
) : MVIBaseViewModel<HomeActions, HomeResults, HomeViewState>() {


    override val defaultViewState: HomeViewState
        get() = HomeViewState()

    override fun handleAction(action: HomeActions): Flow<HomeResults> = flow {

        when (action) {

            is HomeActions.IdentifyPhoto -> {
                emit(HomeResults.IdentifyPhoto(CommonViewState(isLoading = true)))
                try {
                    val result = backEndServices.identifyMonument(action.file)
                    emit(HomeResults.IdentifyPhoto(CommonViewState(data = result)))
                } catch (e: Exception) {
                    emit(HomeResults.IdentifyPhoto(CommonViewState(errorThrowable = e)))
                }
            }

            is HomeActions.GetCurrentLocation -> {
                try {
                    getCurrentLocationUseCase().collect { latLng ->
                        emit(HomeResults.CurrentLocation(CommonViewState(data = latLng)))

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            is HomeActions.SelectGenre -> {
                emit(HomeResults.SelectGenre(action.genre))
                
                val placeType = when (action.genre.placeTypes) {
                    "hotels" -> "hotel"
                    else -> action.genre.placeTypes
                }
                handleGetPlaces(action.location, this, placeType)
            }

            is HomeActions.ResetIdentificationResult -> {
                emit(HomeResults.ResetIdentificationResult)
            }
        }

    }


    private suspend fun handleGetPlaces(
        currentLocation: LatLng,
        collector: FlowCollector<HomeResults>,
        placesTypes: String
    ) {
        collector.emit(HomeResults.GetPlaces(CommonViewState(isLoading = true)))

        val result = nearBySearchUseCase(currentLocation, listOf(placesTypes))

        collector.emit(HomeResults.GetPlaces(CommonViewState(data = result)))

    }

}
