package com.besha.egyptguide.features.profile.presenation.viewmodel

import com.besha.egyptguide.appcore.data.model.DataState
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.besha.egyptguide.appcore.mvi.MVIBaseViewModel
import com.besha.egyptguide.appcore.notification.alarm.AlarmItem
import com.besha.egyptguide.appcore.notification.alarm.AlarmScheduler
import com.besha.egyptguide.features.profile.data.model.UserProfile
import com.besha.egyptguide.features.profile.domain.usecase.GetProfileUseCase
import com.besha.egyptguide.features.profile.domain.usecase.LogOutUseCase
import com.besha.egyptguide.features.profile.domain.usecase.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val alarmScheduler: AlarmScheduler
): MVIBaseViewModel<ProfileActions, ProfileResults, ProfileViewState>() {

    override val defaultViewState: ProfileViewState
        get() = ProfileViewState()

    override fun handleAction(action: ProfileActions): Flow<ProfileResults> = flow {
        when (action) {

            is ProfileActions.GetProfile -> {
                handleGetProfile(this)
            }

            is ProfileActions.UpdateProfile -> {
                updateProfileUseCase(action.updateProfileRequest)
                handleGetProfile(this)
            }

            is ProfileActions.LogOut -> {
              logOutUseCase()
            }

            is ProfileActions.ToggleNotification -> {
                emit(ProfileResults.NotificationResult(action.notification))
            }

            is ProfileActions.ScheduleFoodAlarms -> {
                val today = LocalDate.now()
                
                val timeAlarm = AlarmItem(
                    time = LocalDateTime.of(today, action.time),
                    foodCategory = action.category,
                    title = "Lunch Time!",
                    message = "How about some ${action.category} for lunch?"
                )

                
                alarmScheduler.schedule(timeAlarm)
            }
        }
    }

    private suspend fun handleGetProfile(collector: FlowCollector<ProfileResults>) {

        val result = getProfileUseCase()

        when (result) {

            is DataState.Success<UserProfile> -> {
                collector.emit(ProfileResults.ProfileResult(CommonViewState(data = result.data)))

            }
            is DataState.Error <UserProfile> -> {
                collector.emit(ProfileResults.ProfileResult(CommonViewState(errorThrowable = result.throwable)))
            }
            else -> {}
        }

    }


}
