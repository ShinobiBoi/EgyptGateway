plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)


    //dagger hilt
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.gms.google.services)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.besha.egyptguide"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.besha.egyptguide"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

}

dependencies {
    implementation (libs.androidx.work.runtime.ktx)

    implementation(libs.coil)


    implementation(libs.core)

    implementation (libs.androidx.datastore.preferences)


    implementation(libs.androidx.foundation) // or latest

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)


    implementation(libs.kotlinx.serialization.json)



    // Dagger Hilt
    implementation(libs.hilt.android)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.compose.foundation)
    ksp(libs.hilt.compiler)

    // Maps SDK for Android
    implementation(libs.play.services.maps)
    implementation(libs.places)
    implementation(libs.maps.compose)

    implementation(libs.hilt.navigation.compose)

    //retrofit
    implementation (libs.gson)
    implementation(libs.retrofit)
    implementation (libs.converter.gson)

    implementation("com.kizitonwose.calendar:compose:2.10.0")
    //coil
    implementation(libs.coil.compose)

    //implementation(libs.glide)

    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockk)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}