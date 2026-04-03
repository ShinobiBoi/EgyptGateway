package com.besha.egyptguide.features.maps.domain.usecases

import com.besha.egyptguide.features.maps.domain.repo.MapsRepo
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class MapsNearBySearchUseCase @Inject constructor(private val mapsRepo: MapsRepo) {
    suspend operator fun invoke(currentLocation: LatLng, types: List<String>) = mapsRepo.nearBySearch(currentLocation,types)
}