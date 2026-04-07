package com.besha.egyptguide.features.quiz.di

import com.besha.egyptguide.features.quiz.data.remote.QuizRemoteClientImp
import com.besha.egyptguide.features.quiz.data.repo.QuizRepoImp
import com.besha.egyptguide.features.quiz.domain.remote.QuizRemoteClient
import com.besha.egyptguide.features.quiz.domain.repo.QuizRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class QuizBind {

    @Binds
    abstract fun bindQuizRepo(quizRepoImp: QuizRepoImp): QuizRepo

    @Binds
    abstract fun bindQuizRemoteClient(quizRemoteClientImp: QuizRemoteClientImp): QuizRemoteClient
}
