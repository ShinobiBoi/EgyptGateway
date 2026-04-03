package com.besha.egyptguide.features.home.data.remote

import com.besha.egyptguide.appcore.data.model.MyPlace
import com.besha.egyptguide.features.home.domain.remote.HomeRemoteClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchResolvedPhotoUriRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HomeRemoteClientImp  @Inject constructor(private val placesClient: PlacesClient) :
    HomeRemoteClient {



    override suspend fun nearBySearch(
        currentLocation: LatLng,
        types: List<String>,
    ): List<MyPlace> {

        val fields = listOf(
            Place.Field.ID,
            Place.Field.DISPLAY_NAME,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.PHOTO_METADATAS,
            Place.Field.LOCATION
        )

        val circle = CircularBounds.newInstance(currentLocation, 5000.0)

        val request = SearchNearbyRequest.builder(circle, fields)
            .setIncludedTypes(types)
            .setMaxResultCount(3)
            .build()

        val result = placesClient.searchNearby(request).await()

        return coroutineScope {

            result.places.map { place ->

                async {
                    mapPlaceToMyPlace(place, fetchPhoto = true)
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

        return MyPlace(
            id = place.id,
            displayName = place.displayName,
            formattedAddress = place.formattedAddress,
            location = place.location,
            imageUri = uri
        )
    }

}