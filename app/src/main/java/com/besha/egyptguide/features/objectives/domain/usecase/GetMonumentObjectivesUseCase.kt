package com.besha.egyptguide.features.objectives.domain.usecase

import com.besha.egyptguide.features.objectives.domain.repo.ObjectivesRepo
import javax.inject.Inject

class GetMonumentObjectivesUseCase @Inject constructor(private val objectivesRepo: ObjectivesRepo) {
    suspend operator fun invoke() = objectivesRepo.getMonumentObjectives()
}
