package com.besha.egyptguide.features.monuments.domain.usecase

import com.besha.egyptguide.features.monuments.domain.repo.MonumentRepo
import javax.inject.Inject

class GetMonumentRatingsUseCase @Inject constructor(private val monumentRepo: MonumentRepo) {
    suspend operator fun invoke(monumentId: String, limit: Int) = monumentRepo.getMonumentRatings(monumentId, limit)
}
