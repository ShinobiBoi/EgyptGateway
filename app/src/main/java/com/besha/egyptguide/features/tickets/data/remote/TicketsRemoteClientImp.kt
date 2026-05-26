package com.besha.egyptguide.features.tickets.data.remote

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.data.remote.BackEndServices
import com.besha.egyptguide.features.tickets.data.dto.GetTicketResponse
import com.besha.egyptguide.features.tickets.data.dto.SubmitTicketRequest
import com.besha.egyptguide.features.tickets.data.dto.SubmitTicketResponse
import com.besha.egyptguide.features.tickets.data.dto.TicketDetailsResponse
import com.besha.egyptguide.features.tickets.domain.remote.TicketsRemoteClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class TicketsRemoteClientImp @Inject constructor(
    private val backEndServices: BackEndServices
) : TicketsRemoteClient {
    override suspend fun submitTicket(submitTicketRequest: SubmitTicketRequest): DataState<SubmitTicketResponse> {


        return try {
            val response = backEndServices.submitTicket(
                name = submitTicketRequest.name.toRequestBody("text/plain".toMediaTypeOrNull()),
                google_place_id = submitTicketRequest.google_place_id.toRequestBody("text/plain".toMediaTypeOrNull()),
                latitude = submitTicketRequest.latitude.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull()),
                longitude = submitTicketRequest.longitude.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull()),
                photo = submitTicketRequest.photo
            )
            if (response.isSuccessful && response.body() != null) {
                DataState.Success(response.body()!!)
            } else {
                DataState.Error(Throwable(response.message()))
            }
        } catch (e: Exception) {
            DataState.Error(Throwable(e.message))
        }
    }


    override suspend fun getTickets(): DataState<GetTicketResponse> {
        return try {
            val result = backEndServices.myTickets()
            if (result.isSuccessful && result.body() != null){
                DataState.Success(result.body()!!)
            }else {
                DataState.Error(Throwable(result.message()))
            }
        } catch (e: Exception) {
            DataState.Error(Throwable(e.message))
        }
    }

    override suspend fun getTicketDetails(ticketId: String): DataState<TicketDetailsResponse> {
        return try {
            val response = backEndServices.getTicketDetails(ticketId)
            if (response.isSuccessful && response.body() != null) {
                DataState.Success(response.body()!!)
            } else {
                DataState.Error(Throwable(response.message()))
            }
        } catch (e: Exception) {
            DataState.Error(Throwable(e.message))
        }
    }
}
