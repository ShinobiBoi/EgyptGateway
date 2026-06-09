package com.besha.egyptguide.features.maps.data.repo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.data.model.MyPlace
import com.besha.egyptguide.features.maps.data.dto.MatrixDto
import com.besha.egyptguide.features.maps.data.dto.RoutesDto
import com.besha.egyptguide.features.maps.data.dto.request.MatrixRequestDto
import com.besha.egyptguide.features.maps.data.dto.request.RoutesRequestDto
import com.besha.egyptguide.features.maps.domain.remote.MapsRemoteClient
import com.besha.egyptguide.features.maps.domain.repo.MapsRepo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class MapsRepoImp @Inject constructor (private val mapsRemoteClient: MapsRemoteClient, @ApplicationContext private val context: Context): MapsRepo {


    override suspend fun onQueryChange(
        newQuery: String,
        sessionToken: AutocompleteSessionToken
    ): List<AutocompletePrediction> {
        return mapsRemoteClient.onQueryChange(newQuery, sessionToken)
    }

    override suspend fun selectPlace(
        placeId: String,
        sessionToken: AutocompleteSessionToken
    ): MyPlace {
        return mapsRemoteClient.selectPlace(placeId, sessionToken)

    }

    override suspend fun searchByText(
        currentLocation: LatLng,
        query: String
    ): List<MyPlace> {
        return mapsRemoteClient.searchByText(currentLocation,query)

    }

    override suspend fun nearBySearch(
        currentLocation: LatLng,
        types: List<String>
    ): List<MyPlace> {
        return mapsRemoteClient.nearBySearch(currentLocation,types)
    }

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

   override fun getCurrentLocationFlow(): Flow<LatLng> = callbackFlow {

        // Permission check
        val permissionCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            close(IllegalAccessException("Location permission not granted"))
            return@callbackFlow
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        ).apply {
            setMinUpdateIntervalMillis(2000L)
        }.build()


        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(LatLng(location.latitude, location.longitude)).isSuccess
                }
            }
        }

        // Start location updates
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            context.mainLooper
        )

        // Ensure we remove updates when flow collector is gone
        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    override suspend fun getMapsRoutes(request: RoutesRequestDto): DataState<RoutesDto> {
        return mapsRemoteClient.getMapsRoutes(request)
    }

    override suspend fun getMapsMatrix(request: MatrixRequestDto): DataState<List<MatrixDto>> {
        return mapsRemoteClient.getMapsMatrix(request)
    }


}