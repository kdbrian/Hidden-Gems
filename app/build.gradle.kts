plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)

    id("kotlin-kapt")
    id("kotlin-parcelize")

    id("com.google.dagger.hilt.android")
}

android {
    namespace = "io.github.junrdev.hiddengems"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.junrdev.hiddengems"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "mapsApiKey", "\"${System.getenv("mapsApiKey") ?: ""}\"")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //auth
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    //firestore
    implementation(libs.firebase.firestore)

    //maps
    implementation(libs.play.services.maps)

    //nav
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    //storage
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)

    //room
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    //circle image view
    implementation(libs.circleimageview)

    //glide
    implementation(libs.glide)

    //hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    //shimmer
    implementation(libs.shimmer)

    //datastore
    implementation(libs.androidx.datastore.preferences)


    //gson
    implementation(libs.gson)

    //okhttp
    implementation(libs.okhttp)


}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}