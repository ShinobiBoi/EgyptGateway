package com.besha.egyptguide.features.monuments.presenation.viewmodel

import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.ViewState
import com.besha.egyptguide.features.monuments.data.dto.MonumentDto
import com.besha.egyptguide.features.monuments.data.dto.RatingDto
import com.besha.egyptguide.features.monuments.data.dto.RatingSummaryDto
import com.besha.egyptguide.features.monuments.domain.model.Rating

data class MonumentViewState(
    val monuments: CommonViewState<List<MonumentDto>> = CommonViewState(),
    val selectedMonument: CommonViewState<MonumentDto> = CommonViewState(),
    val ratings: CommonViewState<List<Rating>> = CommonViewState(),
    val ratingSummary: CommonViewState<RatingSummaryDto> = CommonViewState()
) : ViewState
