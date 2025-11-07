plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // The kotlin.compose plugin alias might not be standard. If you get an error here,
    // you might need to check your libs.versions.toml file.
    // However, the main error is fixed below.
}
@Suppress("UnstableApiUsage")
android {
    namespace = "com.example.stepattendanceapp"
    compileSdk = 36 // Corrected syntax

    defaultConfig {
        applicationId = "com.example.stepattendanceapp"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    }
    // This is the correct block for the compiler extension version
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Your dependencies are already correctly defined here using the version catalog
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // These older view-system dependencies can often be removed if you are only using Compose
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    // implementation(libs.androidx.activity) // This is likely redundant with activity-compose
    implementation(libs.androidx.constraintlayout)
    //
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.activity)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Retrofit for networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Moshi for JSON parsing
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    //
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    // For loading images from a URL (like the QR code)
    implementation("io.coil-kt:coil-compose:2.6.0")




}