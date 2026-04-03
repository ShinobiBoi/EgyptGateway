package com.besha.egyptguide.features.maps.domain.repo

import com.besha.egyptguide.appcore.data.model.MyPlace
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import kotlinx.coroutines.flow.Flow

interface MapsRepo {
    suspend fun  onQueryChange(newQuery: String, sessionToken: AutocompleteSessionToken): List<AutocompletePrediction>

    suspend fun selectPlace(placeId: String, sessionToken: AutocompleteSessionToken): MyPlace


    suspend fun searchByText(currentLocation: LatLng,query : String): List<MyPlace>

    suspend fun nearBySearch(currentLocation: LatLng, types: List<String>): List<MyPlace>

    fun getCurrentLocationFlow(): Flow<LatLng>


}