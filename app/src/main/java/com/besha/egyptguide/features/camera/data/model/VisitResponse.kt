package com.besha.egyptguide.features.camera.data.model

data class VisitResponse(
    val message: String,
    val visit_intent: Boolean,
    val earned_points : Int,
    val total_points : Int
)