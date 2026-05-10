package com.besha.egyptguide.features.camera.di

import com.besha.egyptguide.features.camera.data.remote.CameraRemoteImp
import com.besha.egyptguide.features.camera.data.repo.CameraRepoImp
import com.besha.egyptguide.features.camera.domain.remote.CameraRemote
import com.besha.egyptguide.features.camera.domain.repo.CameraRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CameraBind {

    @Binds
    abstract fun bindCameraRemote(cameraRemoteImp: CameraRemoteImp): CameraRemote


    @Binds
    abstract fun bindCameraRepo(cameraRepoImp: CameraRepoImp): CameraRepo
}
