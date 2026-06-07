package com.besha.egyptguide.features.monuments.presenation.viewmodel

import com.besha.egyptguide.appcore.mvi.Action

sealed class MonumentActions : Action {
    object GetMonuments : MonumentActions()
    data class GetMonumentDetails(val monumentId: String) : MonumentActions()
    data class GetMoreRatings(val monumentId: String, val limit: Int) : MonumentActions()
}
