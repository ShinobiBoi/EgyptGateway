package com.besha.egyptguide.features.monuments.domain.usecase

import com.besha.egyptguide.features.monuments.domain.repo.MonumentRepo
import javax.inject.Inject

class GetMonumentRatingSummaryUseCase @Inject constructor(private val monumentRepo: MonumentRepo) {
    suspend operator fun invoke(monumentId: String) = monumentRepo.getMonumentRatingSummary(monumentId)
}
