plugins {
    alias(libs.plugins.android.application)
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
    implementation(libs.androidx.annotation)
    implementation ("androidx.work:work-runtime-ktx:2.8.1")

    implementation("androidx.core:core-ktx:1.12.0") // For ContextCompat and core features
    implementation("androidx.annotation:annotation:1.7.0") // For NonNull and other annotations
    implementation("androidx.fragment:fragment-ktx:1.6.1") // For Fragment utilities
    implementation (libs.material.v190)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.activity.ktx)
    implementation(libs.activity.compose)
    implementation(libs.biometric)
    implementation(libs.flexbox)
    implementation(libs.gson)
    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view)
    implementation (libs.androidx.camera.extensions)
    implementation(libs.camera.core)
    implementation(libs.camera.view)
    implementation(libs.camera.lifecycle)
    implementation(libs.androidx.recyclerview)
    implementation(libs.glide)
    annotationProcessor(libs.glideCompiler)
    implementation(libs.mpandroidchart)
    implementation(libs.work.runtime)
}