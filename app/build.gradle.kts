plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.muszaki_shop"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.muszaki_shop"
        minSdk = 25
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation(libs.firebase.auth)
    implementation("com.google.firebase:firebase-analytics")
    
    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    
    // Animations
    implementation(libs.lottie)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.gson)
    implementation(libs.glide)
    implementation ("com.github.ismaeldivita:chip-navigation-bar:1.4.0")
}