package com.besha.egyptguide.features.maps.domain.usecases

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.maps.data.dto.RoutesDto
import com.besha.egyptguide.features.maps.data.dto.request.RoutesRequestDto
import com.besha.egyptguide.features.maps.domain.repo.MapsRepo
import javax.inject.Inject

class GetMapsRoutesUseCase @Inject constructor(private val mapsRepo: MapsRepo) {
    suspend operator fun invoke(request: RoutesRequestDto): DataState<RoutesDto> {
        return mapsRepo.getMapsRoutes(request)
    }
}
