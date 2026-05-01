package com.besha.egyptguide.features.quiz.data.remote

import android.util.Log
import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.data.remote.BackEndServices
import com.besha.egyptguide.features.quiz.data.model.Quiz
import com.besha.egyptguide.features.quiz.data.model.QuizItem
import com.besha.egyptguide.features.quiz.data.model.RatingRequest
import com.besha.egyptguide.features.quiz.data.model.RatingResponse
import com.besha.egyptguide.features.quiz.data.model.SubmitQuizRequest
import com.besha.egyptguide.features.quiz.data.model.SubmitQuizResponse
import com.besha.egyptguide.features.quiz.data.model.VisitRequest
import com.besha.egyptguide.features.quiz.data.model.VisitResponse
import com.besha.egyptguide.features.quiz.domain.remote.QuizRemoteClient
import javax.inject.Inject

class QuizRemoteClientImp @Inject constructor(private val backEndServices: BackEndServices) :
    QuizRemoteClient {

    override suspend fun getQuiz(id: String): DataState<Quiz> {

        Log.d("TAG", id)

        if (id == "dc95690b-9757-4a6e-835b-03133cc7e2ac") {
            return DataState.Success(
                Quiz().apply {
                    add(
                        QuizItem(
                            id = "1",
                            monument_id = "m1",
                            question = "question answer a",
                            options = listOf("a", "v", "c ", "d"),
                            correct_answer = "a"
                        )
                    )
                    add(
                        QuizItem(
                            id = "2",
                            monument_id = "m1",
                            question = "question answer b",
                            options = listOf("a", "v", "c ", "d"),
                            correct_answer = "b"
                        )
                    )
                    add(
                        QuizItem(
                            id = "3",
                            question = "question answer c",
                            options = listOf("a", "v", "c ", "d"),
                            correct_answer = "c"
                        )
                    )
                    add(
                        QuizItem(
                            id = "4",
                            question = "question answer d",
                            options = listOf("a", "v", "c ", "d"),
                            correct_answer = "d"
                        )
                    )
                }
            )
        }
        return DataState.Error(Exception())


        /*
        val result = backEndServices.getQuiz(id)

        if (result.isSuccessful){
            return DataState.Success(result.body()!!)
        }else
            return DataState.Error(Exception())
*/

    }

    override suspend fun visit(visitRequest: VisitRequest): DataState<VisitResponse> {
        return try {
            val response = backEndServices.gamVisit(visitRequest.monument_id)
            if (response.isSuccessful && response.body() != null) {
                DataState.Success(response.body()!!)
            }
            else{
                DataState.Error(Exception(response.errorBody()!!.string()))
            }
        }catch (e:Exception){
            DataState.Error(e)
        }
    }

    override suspend fun submitQuiz(submitQuizRequest: SubmitQuizRequest): DataState<SubmitQuizResponse> {
        return try {
            val response = backEndServices.gamSubmitQuiz(
                id = submitQuizRequest.monument_id,
                score = submitQuizRequest.score
            )
            if (response.isSuccessful && response.body() != null) {
                DataState.Success(response.body()!!)
            }
            else{
                DataState.Error(Exception(response.errorBody()!!.string()))
            }
        }catch (e:Exception){
            DataState.Error(e)
        }
    }

    override suspend fun rate(ratingRequest: RatingRequest): DataState<RatingResponse> {
        return try {
            val response = backEndServices.gamRating(
                id = ratingRequest.monument_id,
                score = ratingRequest.rating,
                crowdLevel = ratingRequest.crowd_level,
                comment = ratingRequest.comment
            )
            if (response.isSuccessful && response.body() != null) {
                DataState.Success(response.body()!!)
            }
            else{
                DataState.Error(Exception(response.errorBody()!!.string()))
            }
        }catch (e:Exception){
            DataState.Error(e)
        }
    }


}
