plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.theremindful2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.theremindful2"
        minSdk = 24
        targetSdk = 34
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.material.v190)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.activity.ktx)
    implementation(libs.activity.compose)
    implementation(libs.biometric)
    implementation(libs.flexbox)
    implementation(libs.gson)

    // Camera dependencies (remove duplicates)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)

    // Firebase (use BOM from version catalog)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    // Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
//    implementation (libs.material.v190)
//    implementation(libs.appcompat)
//    implementation(libs.material)
//    implementation(libs.navigation.ui)
//    implementation(libs.firebase.crashlytics.buildtools)
//    implementation(libs.firebase.storage)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.ext.junit)
//    androidTestImplementation(libs.espresso.core)
//    implementation(libs.activity.ktx)
//    implementation(libs.activity.compose)
//    implementation(libs.biometric)
//    implementation(libs.flexbox)
//    implementation(libs.gson)
//    implementation (libs.androidx.camera.core)
//    implementation (libs.androidx.camera.camera2)
//    implementation (libs.androidx.camera.lifecycle)
//    implementation (libs.androidx.camera.view)
//    implementation (libs.androidx.camera.extensions)
//    implementation(libs.camera.core)
//    implementation(libs.camera.view)
//    implementation(libs.camera.lifecycle)
//    implementation(platform(libs.firebase.bom))
//    implementation(libs.firebase.database)
//    implementation(libs.firebase.firestore)
//    implementation(libs.room.runtime)
//    annotationProcessor(libs.room.compiler)
//    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
//    implementation("com.google.firebase:firebase-analytics")
}