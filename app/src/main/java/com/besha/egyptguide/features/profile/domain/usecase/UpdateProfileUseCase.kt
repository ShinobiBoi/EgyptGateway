package com.besha.egyptguide.features.profile.domain.usecase

import com.besha.egyptguide.features.profile.data.model.UpdateProfileRequest
import com.besha.egyptguide.features.profile.domain.repo.ProfileRepo
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(private val profileRepo: ProfileRepo) {
    suspend operator fun invoke( updateProfileRequest: UpdateProfileRequest) = profileRepo.updateProfile(updateProfileRequest)
}
