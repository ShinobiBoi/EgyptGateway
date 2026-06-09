package com.besha.egyptguide.features.maps.presentaion.viewmodel


import android.util.Log
import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.MVIBaseViewModel
import com.besha.egyptguide.features.maps.data.dto.request.Destination
import com.besha.egyptguide.features.maps.data.dto.request.DestinationX
import com.besha.egyptguide.features.maps.data.dto.request.Location
import com.besha.egyptguide.features.maps.data.dto.request.MatrixRequestDto
import com.besha.egyptguide.features.maps.data.dto.request.MyLatLng
import com.besha.egyptguide.features.maps.data.dto.request.Origin
import com.besha.egyptguide.features.maps.data.dto.request.OriginX
import com.besha.egyptguide.features.maps.data.dto.request.RoutesRequestDto
import com.besha.egyptguide.features.maps.data.dto.request.Waypoint
import com.besha.egyptguide.features.maps.domain.model.MapsPlace
import com.besha.egyptguide.features.maps.domain.usecases.CurrentLocationUseCase
import com.besha.egyptguide.features.maps.domain.usecases.GetMapsMatrixUseCase
import com.besha.egyptguide.features.maps.domain.usecases.GetMapsRoutesUseCase
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
    private val mapsNearBySearchUseCase: MapsNearBySearchUseCase,
    private val getMapsRoutesUseCase: GetMapsRoutesUseCase,
    private val getMapsMatrixUseCase: GetMapsMatrixUseCase
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

            is MapsActions.GetMapsRoutes -> {
                handleGetMapsRoutes(action.destination, action.origin, action.travelMode, this)
            }

        }

    }

    private suspend fun handleGetMapsRoutes(
        destination: MyLatLng,
        origin: MyLatLng,
        travelMode: com.besha.egyptguide.features.maps.data.dto.request.TravelMode,
        collector: FlowCollector<MapsResults>
    ) {
        collector.emit(MapsResults.Routes(CommonViewState(isLoading = true)))
        try {
            Log.d("routes", "1")

            val request = RoutesRequestDto(
                destination = Destination(
                    location = Location(
                        latLng = MyLatLng(destination.latitude, destination.longitude)
                    )
                ),
                origin = Origin(
                    location = Location(
                        latLng = MyLatLng(origin.latitude, origin.longitude)
                    )
                ),
                travelMode = travelMode
            )
            val result = getMapsRoutesUseCase(request)

            when(result){
                is DataState.Success -> {
                    Log.d("routes", "2")
                    collector.emit(MapsResults.Routes(CommonViewState(data = result.data)))

                }
                is DataState.Error -> {
                    Log.d("routes", "3")
                    collector.emit(MapsResults.Routes(CommonViewState(errorThrowable = result.throwable)))
                }
                else -> {}
            }

        } catch (e: Exception) {
            Log.d("routes", "2 error")
            Log.d("routes", "${e.message}")

            collector.emit(MapsResults.Routes(CommonViewState(errorThrowable = Throwable(message = e.message ?: "Unknown Error"))))
        }
    }

    private suspend fun handleNearBySearch(
        currentLocation: LatLng,
        types: List<String>,
        collector: FlowCollector<MapsResults>
    ) {
        collector.emit(MapsResults.NearByPlaces(CommonViewState(isLoading = true)))
        val nearbyResults = mapsNearBySearchUseCase(currentLocation, types)

        if (nearbyResults.isEmpty()) {
            collector.emit(MapsResults.NearByPlaces(CommonViewState(data = emptyList())))
            return
        }

        val matrixRequest = MatrixRequestDto(
            origins = listOf(
                OriginX(
                    waypoint = Waypoint(
                        location = Location(
                            latLng = MyLatLng(currentLocation.latitude, currentLocation.longitude)
                        )
                    )
                )
            ),
            destinations = nearbyResults.map { place ->
                DestinationX(
                    waypoint = Waypoint(
                        location = Location(
                            latLng = MyLatLng(
                                place.location?.latitude ?: 0.0,
                                place.location?.longitude ?: 0.0
                            )
                        )
                    )
                )
            }
        )

        val matrixResult = getMapsMatrixUseCase(matrixRequest)

        when (matrixResult) {
            is DataState.Success -> {
                val mapsPlaces = nearbyResults.mapIndexed { index, myPlace ->
                    val matrixData = matrixResult.data.find { it.destinationIndex == index }
                    MapsPlace(
                        id = myPlace.id,
                        displayName = myPlace.displayName,
                        formattedAddress = myPlace.formattedAddress,
                        location = myPlace.location,
                        imageUri = myPlace.imageUri,
                        distanceMeters = matrixData?.distanceMeters,
                        duration = matrixData?.duration
                    )
                }.sortedBy { mapsPlace ->
                    mapsPlace.duration?.removeSuffix("s")?.toDoubleOrNull() ?: Double.MAX_VALUE
                }
                collector.emit(MapsResults.NearByPlaces(CommonViewState(data = mapsPlaces)))
            }
            is DataState.Error -> {
                val mapsPlaces = nearbyResults.map { myPlace ->
                    MapsPlace(
                        id = myPlace.id,
                        displayName = myPlace.displayName,
                        formattedAddress = myPlace.formattedAddress,
                        location = myPlace.location,
                        imageUri = myPlace.imageUri
                    )
                }
                collector.emit(MapsResults.NearByPlaces(CommonViewState(data = mapsPlaces, errorThrowable = matrixResult.throwable)))
            }
            else -> {
                val mapsPlaces = nearbyResults.map { myPlace ->
                    MapsPlace(
                        id = myPlace.id,
                        displayName = myPlace.displayName,
                        formattedAddress = myPlace.formattedAddress,
                        location = myPlace.location,
                        imageUri = myPlace.imageUri
                    )
                }
                collector.emit(MapsResults.NearByPlaces(CommonViewState(data = mapsPlaces)))
            }
        }
    }

    private suspend fun handleTextSearchBy(
        currentLocation: LatLng,
        query: String,
        collector: FlowCollector<MapsResults>
    ) {
        collector.emit(MapsResults.NearByPlaces(CommonViewState(isLoading = true)))
        val searchResults = textSearchByUseCase(currentLocation, query)

        if (searchResults.isEmpty()) {
            collector.emit(MapsResults.NearByPlaces(CommonViewState(data = emptyList())))
            return
        }

        val matrixRequest = MatrixRequestDto(
            origins = listOf(
                OriginX(
                    waypoint = Waypoint(
                        location = Location(
                            latLng = MyLatLng(currentLocation.latitude, currentLocation.longitude)
                        )
                    )
                )
            ),
            destinations = searchResults.map { place ->
                DestinationX(
                    waypoint = Waypoint(
                        location = Location(
                            latLng = MyLatLng(
                                place.location?.latitude ?: 0.0,
                                place.location?.longitude ?: 0.0
                            )
                        )
                    )
                )
            }
        )

        val matrixResult = getMapsMatrixUseCase(matrixRequest)

        when (matrixResult) {
            is DataState.Success -> {
                val mapsPlaces = searchResults.mapIndexed { index, myPlace ->
                    val matrixData = matrixResult.data.find { it.destinationIndex == index }
                    MapsPlace(
                        id = myPlace.id,
                        displayName = myPlace.displayName,
                        formattedAddress = myPlace.formattedAddress,
                        location = myPlace.location,
                        imageUri = myPlace.imageUri,
                        distanceMeters = matrixData?.distanceMeters,
                        duration = matrixData?.duration
                    )
                }.sortedBy { mapsPlace ->
                    mapsPlace.duration?.removeSuffix("s")?.toDoubleOrNull() ?: Double.MAX_VALUE
                }
                collector.emit(MapsResults.NearByPlaces(CommonViewState(data = mapsPlaces)))
            }
            is DataState.Error -> {
                val mapsPlaces = searchResults.map { myPlace ->
                    MapsPlace(
                        id = myPlace.id,
                        displayName = myPlace.displayName,
                        formattedAddress = myPlace.formattedAddress,
                        location = myPlace.location,
                        imageUri = myPlace.imageUri
                    )
                }
                collector.emit(MapsResults.NearByPlaces(CommonViewState(data = mapsPlaces, errorThrowable = matrixResult.throwable)))
            }
            else -> {
                val mapsPlaces = searchResults.map { myPlace ->
                    MapsPlace(
                        id = myPlace.id,
                        displayName = myPlace.displayName,
                        formattedAddress = myPlace.formattedAddress,
                        location = myPlace.location,
                        imageUri = myPlace.imageUri
                    )
                }
                collector.emit(MapsResults.NearByPlaces(CommonViewState(data = mapsPlaces)))
            }
        }
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

            Log.d("TAG",place.imageUri.toString())
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
