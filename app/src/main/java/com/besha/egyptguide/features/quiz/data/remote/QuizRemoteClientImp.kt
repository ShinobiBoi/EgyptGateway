package com.besha.egyptguide.features.quiz.data.remote

import android.util.Log
import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.data.remote.BackEndServices
import com.besha.egyptguide.features.quiz.data.model.Quiz
import com.besha.egyptguide.features.quiz.data.model.QuizItem
import com.besha.egyptguide.features.quiz.domain.remote.QuizRemoteClient
import javax.inject.Inject

class QuizRemoteClientImp @Inject constructor(private val backEndServices: BackEndServices) :
    QuizRemoteClient {

    override suspend fun getQuiz(id: String): DataState<Quiz> {

        Log.d("TAG", id)

        if (id == "940bbe59-df90-4739-8c46-42c7e3e870e2") {
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



}
