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

            is HomeActions.GetCafePlaces -> {
                handleGetPlaces(action.currentLocation, this, PlaceTypes.CAFE)
            }

            is HomeActions.GetMallPlaces -> {
                handleGetPlaces(action.currentLocation, this,PlaceTypes.SHOPPING_MALL)
            }

            is HomeActions.GetHotelPlaces -> {
                handleGetPlaces(action.currentLocation, this,"hotel")
            }

            is HomeActions.GetRestaurantsPlaces -> {
                handleGetPlaces(action.currentLocation, this,PlaceTypes.RESTAURANT)
            }
            is HomeActions.IdentifyPhoto -> {
                val result = backEndServices.identifyMonument(action.file)
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
