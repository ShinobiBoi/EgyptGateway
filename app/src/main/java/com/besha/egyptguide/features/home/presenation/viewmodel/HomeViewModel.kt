package com.besha.egyptguide.features.home.presenation.viewmodel

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.data.remote.BackEndServices
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.MVIBaseViewModel
import com.besha.egyptguide.features.home.domain.usecase.NearBySearchUseCase
import com.besha.egyptguide.features.maps.domain.usecases.CurrentLocationUseCase
import com.besha.egyptguide.features.monuments.domain.usecase.GetMonumentsUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val nearBySearchUseCase: NearBySearchUseCase,
    private val getMonumentsUseCase: GetMonumentsUseCase,
    private val getCurrentLocationUseCase: CurrentLocationUseCase,
) : MVIBaseViewModel<HomeActions, HomeResults, HomeViewState>() {


    override val defaultViewState: HomeViewState
        get() = HomeViewState()

    override fun handleAction(action: HomeActions): Flow<HomeResults> = flow {

        when (action) {

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

            is HomeActions.GetMonuments ->{
                handleGetMonuments(this)
            }
        }

    }

    private suspend fun handleGetMonuments(collector: FlowCollector<HomeResults>) {

        collector.emit(HomeResults.GetMonuments(CommonViewState(isLoading = true)))

        when (val result = getMonumentsUseCase()){

            is DataState.Success -> {
                collector.emit(HomeResults.GetMonuments(CommonViewState(data = result.data)))

            }
            is DataState.Error -> {
                collector.emit(HomeResults.GetMonuments(CommonViewState(errorThrowable = result.throwable)))

            }
            else -> {}

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
