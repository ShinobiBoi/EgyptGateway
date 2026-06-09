package com.besha.egyptguide.features.maps.presentaion.viewmodel

import com.besha.egyptguide.appcore.mvi.Action
import com.besha.egyptguide.features.maps.data.dto.request.MyLatLng
import com.besha.egyptguide.features.maps.data.dto.request.TravelMode
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken

sealed class MapsActions() : Action {

    data class OnQueryChange(val newQuery: String, val sessionToken: AutocompleteSessionToken?) : MapsActions()

    data class SelectPlace(val placeId: String, val sessionToken: AutocompleteSessionToken?) : MapsActions()


    object EmptySelectedPlace : MapsActions()

    data class SearchByText(val currentLocation: LatLng, val query : String) : MapsActions()

    data class NearBySearch(val currentLocation: LatLng, val types: List<String>) : MapsActions()

    object EmptyNearBySearch : MapsActions()

    data class ResetState(val sessionToken: AutocompleteSessionToken?,val currentLocation: LatLng,val isLocationLoaded : Boolean) : MapsActions()

    object GetCurrentLocation : MapsActions()
    object CurrentLocationLoaded : MapsActions()

    data class GetMapsRoutes(
        val destination: MyLatLng,
        val origin: MyLatLng,
        val travelMode: TravelMode = TravelMode.DRIVE
    ) : MapsActions()


}