plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.secrets.gradle)
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("D:\\AndroidStudioProjects\\Sokhanyar\\AppKey.jks")
            storePassword = "SalTech#1402"
            keyAlias = "PuyaKhan"
            keyPassword = "SalTech#1402"
        }
        create("release") {
            storeFile = file("D:\\AndroidStudioProjects\\Sokhanyar\\AppKey.jks")
            storePassword = "SalTech#1402"
            keyAlias = "PuyaKhan"
            keyPassword = "SalTech#1402"
        }
    }
    namespace = "ir.saltech.sokhanyar"
    compileSdk = 35

    defaultConfig {
        applicationId = "ir.saltech.sokhanyar"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "1.5.9.152"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("debug")
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui.text.fonts)
    implementation(libs.androidx.material3)
    implementation(platform(libs.openai.client.bom))
    implementation(libs.openai.client)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.runtime.livedata)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.gson)
    implementation(libs.kotlinx.datetime)
    implementation(libs.generativeai)
    implementation(libs.datastore.preferences)
    implementation(libs.ratingbar.compose)
    implementation(libs.picasso)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.compose.webview)
    implementation(libs.mdparserkitcore)
    implementation(libs.dotlottie.android)
    implementation(libs.androidx.emojipicker)
    implementation(libs.androidx.emoji2.views.helper)
    implementation(libs.androidx.documentfile)
    implementation(libs.commons.io)
    runtimeOnly(libs.ktor.client.okhttp)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}