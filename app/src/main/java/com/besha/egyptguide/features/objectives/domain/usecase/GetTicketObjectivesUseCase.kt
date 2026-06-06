package com.besha.egyptguide.features.objectives.domain.usecase

import com.besha.egyptguide.features.objectives.domain.repo.ObjectivesRepo
import javax.inject.Inject

class GetTicketObjectivesUseCase @Inject constructor(private val objectivesRepo: ObjectivesRepo) {
    suspend operator fun invoke() = objectivesRepo.getTicketObjectives()
}
