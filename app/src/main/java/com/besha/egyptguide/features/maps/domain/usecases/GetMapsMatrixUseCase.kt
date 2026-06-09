package com.besha.egyptguide.features.maps.domain.usecases

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.features.maps.data.dto.MatrixDto
import com.besha.egyptguide.features.maps.data.dto.RoutesDto
import com.besha.egyptguide.features.maps.data.dto.request.MatrixRequestDto
import com.besha.egyptguide.features.maps.data.dto.request.RoutesRequestDto
import com.besha.egyptguide.features.maps.domain.repo.MapsRepo
import javax.inject.Inject

class GetMapsMatrixUseCase @Inject constructor(private val mapsRepo: MapsRepo) {
    suspend operator fun invoke(request: MatrixRequestDto): DataState<List<MatrixDto>> {
        return mapsRepo.getMapsMatrix(request)
    }
}
