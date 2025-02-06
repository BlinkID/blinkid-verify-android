plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.microblink.blinkidverify.sample"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.microblink.blinkidverify.sample"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":lib-common")) {
        isTransitive = true
    }
    implementation(libs.blinkid.verify.ux)
    implementation(libs.androidx.navigation.compose)
/**
    // use following set of dependencies if you want to use blinkid-verify-ux library module
    // instead of maven dependency, and remove implementation(libs.blinkid.verify.ux) dependency
    implementation(project(":blinkid-verify-ux"))
    implementation(libs.blinkid.verify.core)
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)
*/
}