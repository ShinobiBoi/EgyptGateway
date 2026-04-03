package com.besha.egyptguide.features.maps.data.remote

import com.besha.egyptguide.appcore.data.model.MyPlace
import com.besha.egyptguide.features.maps.domain.remote.MapsRemoteClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchResolvedPhotoUriRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class MapsRemoteClientImp @Inject constructor(private val placesClient: PlacesClient) :
    MapsRemoteClient {


    override suspend fun onQueryChange(
        newQuery: String,
        sessionToken: AutocompleteSessionToken
    ): List<AutocompletePrediction> {

        if (newQuery.isBlank()) {
            return emptyList()
        }


        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(newQuery)
            .setCountries("EG")
            .setSessionToken(sessionToken)
            .build()

        val result = placesClient.findAutocompletePredictions(request).await()

        return result.autocompletePredictions

    }

    override suspend fun selectPlace(
        placeId: String,
        sessionToken: AutocompleteSessionToken
    ): MyPlace {

        val fields = listOf(
            Place.Field.ID,
            Place.Field.DISPLAY_NAME,
            Place.Field.LOCATION,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.PHOTO_METADATAS // include if you want photo option
        )

        val request = FetchPlaceRequest.builder(placeId, fields)
            .setSessionToken(sessionToken)
            .build()

        val place = placesClient.fetchPlace(request)
            .await()
            .place

        return mapPlaceToMyPlace(place, fetchPhoto = true)
    }

    override suspend fun searchByText(
        currentLocation: LatLng,
        query: String
    ): List<MyPlace> {

        val fields = listOf(
            Place.Field.ID,
            Place.Field.DISPLAY_NAME,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.LOCATION
        )

        val circle = CircularBounds.newInstance(currentLocation, 5000.0)

        val request = SearchByTextRequest.builder(query, fields)
            .setLocationBias(circle)
            .setMaxResultCount(10)
            .build()

        val result = placesClient.searchByText(request).await()

        return coroutineScope {

            result.places.map { place ->

                async {
                    mapPlaceToMyPlace(place, fetchPhoto = false)
                }
            }.awaitAll()
        }
    }



    override suspend fun nearBySearch(
        currentLocation: LatLng,
        types: List<String>,
    ): List<MyPlace> {

        val fields = listOf(
            Place.Field.ID,
            Place.Field.DISPLAY_NAME,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.LOCATION
        )

        val circle = CircularBounds.newInstance(currentLocation, 5000.0)

        val request = SearchNearbyRequest.builder(circle, fields)
            .setIncludedTypes(types)
            .setMaxResultCount(10)
            .build()

        val result = placesClient.searchNearby(request).await()

        return coroutineScope {

            result.places.map { place ->

                async {
                    mapPlaceToMyPlace(place, fetchPhoto = false)
                }
            }.awaitAll()
        }
    }



    private suspend fun mapPlaceToMyPlace(
        place: Place,
        fetchPhoto: Boolean = false
    ): MyPlace {

        val uri = if (fetchPhoto) {
            place.photoMetadatas
                ?.firstOrNull()
                ?.let { metadata ->
                    try {
                    val resolvedResponse = placesClient
                        .fetchResolvedPhotoUri(
                            FetchResolvedPhotoUriRequest.builder(metadata).build()
                        )
                        .await()

                        resolvedResponse.uri

                    } catch (e: Exception) {
                        null
                    }


                }
        } else {
            null
        }

        var governorate = ""
        place.addressComponents?.asList()?.forEach {component ->

            if (component.types.contains("administrative_area_level_1")) {
                governorate = component.name

            }

        }

        return MyPlace(
            id = place.id,
            displayName = place.displayName,
            formattedAddress = place.formattedAddress,
            location = place.location,
            imageUri = uri
        )
    }

}