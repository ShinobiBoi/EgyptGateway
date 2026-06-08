package com.besha.egyptguide.appcore.data.remote

import com.besha.egyptguide.auth.screens.signup.data.model.SignUpRequest
import com.besha.egyptguide.auth.screens.signup.data.model.SignUpResponse
import com.besha.egyptguide.features.camera.data.dto.RatingRequest
import com.besha.egyptguide.features.camera.data.dto.RatingResponse
import com.besha.egyptguide.features.leaderboard.data.model.LeaderboardList
import com.besha.egyptguide.features.leaderboard.data.model.LeaderboardListItem
import com.besha.egyptguide.features.profile.data.dto.UserProfile
import com.besha.egyptguide.features.quiz.data.model.Quiz
import com.besha.egyptguide.features.quiz.data.model.SubmitQuizResponse
import com.besha.egyptguide.features.camera.data.dto.VisitRequest
import com.besha.egyptguide.features.camera.data.dto.VisitResponse
import com.besha.egyptguide.features.monuments.data.dto.MonumentDto
import com.besha.egyptguide.features.monuments.data.dto.RatingDto
import com.besha.egyptguide.features.monuments.data.dto.RatingSummaryDto
import com.besha.egyptguide.features.objectives.data.dto.MonumentObjectivesResponse
import com.besha.egyptguide.features.objectives.data.dto.TicketObjectivesResponse
import com.besha.egyptguide.features.quiz.data.model.SubmitQuizRequest
import com.besha.egyptguide.features.tickets.data.dto.GetTicketResponse
import com.besha.egyptguide.features.tickets.data.dto.SubmitTicketResponse
import com.besha.egyptguide.features.tickets.data.dto.TicketDetailsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface BackEndServices {



    //Auth/////////////////////////////////////////////////////////

    @POST("auth/signup")
    suspend fun signUp(
        @Header("Authorization") token: String,
        @Body signUpRequest: SignUpRequest
    ): Response<SignUpResponse>

    //user///////////////////////////////////////////////////////

    @GET("users/me")
    suspend fun getUser(): Response<UserProfile>


    @Multipart
    @PUT("users/me")
    suspend fun updateProfile(
        @Part("name") name: RequestBody?,
        @Part("language") language: RequestBody?,
        @Part("location") location: RequestBody?,
        @Part("gender") gender: RequestBody?,
        @Part photo: MultipartBody.Part?
    )




    //gamfication///////////////////////////////////////////////////
    @POST("/gamification/visit")
    suspend fun gamVisit(
        @Body visitRequest: VisitRequest,
    ): Response<VisitResponse>

    @POST("/gamification/quiz")
    suspend fun gamSubmitQuiz(
        @Body submitQuizRequest: SubmitQuizRequest,
    ): Response<SubmitQuizResponse>

    @POST("/gamification/rating")
    suspend fun gamRating(
        @Body ratingRequest: RatingRequest,
        ): Response<RatingResponse>

    @GET("/gamification/quizzes")
    suspend fun getQuiz(
        @Query("monument_id") id: String
    ): Response<Quiz>






    //leaderboard///////////////////////////////////////////////////
    @GET("/leaderboard")
    suspend fun getLeaderboard():Response<LeaderboardList>


    @GET("/leaderboard/me")
    suspend fun getCurrentRank():Response<LeaderboardListItem>



    //monument///////////////////////////////////////////////////
    @Multipart
    @POST("monuments/identify")
    suspend fun identifyMonument(
        @Part file: MultipartBody.Part
    ): Response<IdentifyResponse>



    @GET("monuments/")
    suspend fun getMonument(): Response<List<MonumentDto>>

    @GET("monuments/{monument_id}")
    suspend fun getMonumentById(
        @Path("monument_id") monumentId: String,
    ): Response<MonumentDto>

    @GET("monuments/{monument_id}/ratings")
    suspend fun monumentRating(
        @Path("monument_id") monumentId: String,
        @Query("limit") limit: Int,
    ): Response<List<RatingDto>>


    @GET("monuments/{monument_id}/ratings/summary")
    suspend fun monumentRatingSummary(
        @Path("monument_id") monumentId: String
    ): Response<RatingSummaryDto>



    //tickets///////////////////////////////////////////////////
    @Multipart
    @POST("tickets/submit")
    suspend fun submitTicket(
        @Part("name") name: RequestBody?,
        @Part("google_place_id") google_place_id : RequestBody?,
        @Part("latitude") latitude : RequestBody?,
        @Part("longitude") longitude : RequestBody?,
        @Part photo: MultipartBody.Part?
    ): Response<SubmitTicketResponse>

    @GET("tickets/my")
    suspend fun myTickets(
    ): Response<GetTicketResponse>

    @GET("tickets/{ticket_id}")
    suspend fun getTicketDetails(
        @Path("ticket_id") ticketId: String
    ): Response<TicketDetailsResponse>

    //objectives///////////////////////////////////////////////////

    @GET("objectives/my")
    suspend fun monumentObjectives(
    ): Response<MonumentObjectivesResponse>

    @GET("objectives/tickets")
    suspend fun ticketObjectives(
    ): Response<TicketObjectivesResponse>


}
data class ScanResponse(
    val isSuccessful: Boolean,
)




data class IdentifyResponse(
    val status: String,
    val monument_id: String?,
    val label_id: String?,
    val name: String?,
    val description: String?,
    val city: String?,
    val location: String?,
    val confidence: Float,
    val is_reliable: Boolean,
    val message: String?
)