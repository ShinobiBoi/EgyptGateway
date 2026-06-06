package com.besha.egyptguide.features.objectives.presentaion.viewmodel

import com.besha.egyptguide.appcore.mvi.Action

sealed class ObjectivesActions : Action {
    object GetMonumentObjectives : ObjectivesActions()
    object GetTicketObjectives : ObjectivesActions()
}
