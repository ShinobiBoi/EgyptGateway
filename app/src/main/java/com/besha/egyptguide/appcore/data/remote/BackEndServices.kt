package com.besha.egyptguide.appcore.data.remote

import com.besha.egyptguide.appcore.data.model.User
import com.besha.egyptguide.features.quiz.data.model.Quiz
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface BackEndServices {


    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @GET("/gamfication/quizzes")
    suspend fun getQuiz(
        @Query("monument_id") id: String
    ): Response<Quiz>



    @Multipart
    @POST("monuments/identify")
    suspend fun identifyMonument(
        @Part file: MultipartBody.Part
    ): IdentifyResponse


}


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