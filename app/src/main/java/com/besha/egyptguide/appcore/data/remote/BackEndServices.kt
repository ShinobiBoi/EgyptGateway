package com.besha.egyptguide.appcore.data.remote

import com.besha.egyptguide.auth.screens.signup.data.model.SignUpRequest
import com.besha.egyptguide.auth.screens.signup.data.model.SignUpResponse
import com.besha.egyptguide.features.leaderboard.data.model.LeaderboardList
import com.besha.egyptguide.features.leaderboard.data.model.LeaderboardListItem
import com.besha.egyptguide.features.profile.data.model.UserProfile
import com.besha.egyptguide.features.quiz.data.model.Quiz
import com.besha.egyptguide.features.camera.data.model.RatingResponse
import com.besha.egyptguide.features.quiz.data.model.SubmitQuizResponse
import com.besha.egyptguide.features.quiz.data.model.VisitResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface BackEndServices {



    //Auth/////////////////////////////////////////////////////////

    @POST("auth/signup")
    suspend fun signUp(
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
    @POST("/gamfication/visit")
    suspend fun gamVisit(
        @Query("monument_id") id: String,
    ): Response<VisitResponse>

    @POST("/gamfication/quiz")
    suspend fun gamSubmitQuiz(
        @Query("monument_id") id: String,
        @Query("score") score: Int
    ): Response<SubmitQuizResponse>

    @POST("/gamfication/rating")
    suspend fun gamRating(
        @Query("monument_id") id: String,
        @Query("rating") score: Int,
        @Query("crowd_level") crowdLevel: String,
        @Query("comment") comment: String,
        ): Response<RatingResponse>

    @GET("/gamfication/quizzes")
    suspend fun getQuiz(
        @Query("monument_id") id: String
    ): Response<Quiz>






    //leaderboard///////////////////////////////////////////////////
    @GET("/leaderboard")
    suspend fun getLeaderboard():Response<LeaderboardList>

    //leaderboard///////////////////////////////////////////////////
    @GET("/leaderboard/me")
    suspend fun getCurrentRank():Response<LeaderboardListItem>




    @Multipart
    @POST("monuments/identify")
    suspend fun identifyMonument(
        @Part file: MultipartBody.Part
    ): IdentifyResponse


    @Multipart
    @POST("tickets/scan")
    suspend fun scanTicket(
        @Part file: MultipartBody.Part
    ): ScanResponse


    @Multipart
    @POST("tickets/get")
    suspend fun getTickets(
        @Part file: MultipartBody.Part
    ): TicketResponse


}
data class ScanResponse(
    val isSuccessful: Boolean,
)

data class TicketResponse(
    val status: String,
    val monument_id: String?,
    val label_id: String?,
    val name: String?,
    val description: String?,
    val city: String?,
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