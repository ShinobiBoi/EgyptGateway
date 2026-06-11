package com.besha.egyptguide.features.quiz.data.remote

import android.util.Log
import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.data.remote.BackEndServices
import com.besha.egyptguide.features.quiz.data.model.Quiz
import com.besha.egyptguide.features.quiz.data.model.SubmitQuizRequest
import com.besha.egyptguide.features.quiz.data.model.SubmitQuizResponse
import com.besha.egyptguide.features.quiz.domain.remote.QuizRemoteClient
import javax.inject.Inject

class QuizRemoteClientImp @Inject constructor(private val backEndServices: BackEndServices) :
    QuizRemoteClient {

    override suspend fun getQuiz(id: String): DataState<Quiz> {

        val result = backEndServices.getQuiz(id)

        if (result.isSuccessful) {
            Log.d("getQuiz", "getQuiz:  sucess${result.body()}")
            return DataState.Success(result.body()!!)
        } else
            Log.d("getQuiz", "getQuiz: failed  ${result.message()}")
            return DataState.Error(Exception())

    }


    override suspend fun submitQuiz(submitQuizRequest: SubmitQuizRequest): DataState<SubmitQuizResponse> {
        return try {
            val response = backEndServices.gamSubmitQuiz(
                submitQuizRequest
            )
            if (response.isSuccessful && response.body() != null) {
                DataState.Success(response.body()!!)
            } else {
                DataState.Error(Exception(response.errorBody()!!.string()))
            }
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }


}
