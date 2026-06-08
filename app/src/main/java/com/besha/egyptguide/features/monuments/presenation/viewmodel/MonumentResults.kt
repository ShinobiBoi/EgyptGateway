package com.besha.egyptguide.features.monuments.presenation.viewmodel

import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.Result
import com.besha.egyptguide.features.monuments.data.dto.MonumentDto
import com.besha.egyptguide.features.monuments.data.dto.RatingDto
import com.besha.egyptguide.features.monuments.data.dto.RatingSummaryDto
import com.besha.egyptguide.features.monuments.domain.model.Rating

sealed class MonumentResults : Result<MonumentViewState> {
    data class MonumentsResult(val result: CommonViewState<List<MonumentDto>>) : MonumentResults() {
        override fun reduce(defaultState: MonumentViewState, oldState: MonumentViewState): MonumentViewState {
            return oldState.copy(monuments = result)
        }
    }

    data class MonumentDetailsResult(val result: CommonViewState<MonumentDto>) : MonumentResults() {
        override fun reduce(defaultState: MonumentViewState, oldState: MonumentViewState): MonumentViewState {
            return oldState.copy(selectedMonument = result)
        }
    }

    data class RatingsResult(val result: CommonViewState<List<Rating>>) : MonumentResults() {
        override fun reduce(defaultState: MonumentViewState, oldState: MonumentViewState): MonumentViewState {
            return oldState.copy(ratings = result)
        }
    }

    data class RatingSummaryResult(val result: CommonViewState<RatingSummaryDto>) : MonumentResults() {
        override fun reduce(defaultState: MonumentViewState, oldState: MonumentViewState): MonumentViewState {
            return oldState.copy(ratingSummary = result)
        }
    }
}
