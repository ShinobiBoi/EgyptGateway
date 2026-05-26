package com.besha.egyptguide.features.tickets.data.dto

import okhttp3.MultipartBody


data class SubmitTicketRequest(
    val name: String,
    val google_place_id: String,
    val latitude: Double,
    val longitude: Double,
    val photo: MultipartBody.Part?
)
