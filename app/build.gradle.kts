plugins {
    id("com.android.application")
    id("kotlin-parcelize")
    kotlin("android")
}

android {
    namespace = "com.samwdev.battlecity"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.samwdev.battlecity"
        minSdk = 23
        targetSdk = 33
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
            proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2022.10.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(Library.ANDROIDX_CORE)
    implementation(Library.ANDROIDX_APPCOMPAT)
    implementation(Library.MATERIAL)
    implementation(Library.ANDROIDX_LIFECYCLE_VIEW_MODEL)
    implementation(Library.COMPOSE_UI)
    implementation(Library.COMPOSE_MATERIAL)
    implementation(Library.COMPOSE_UI_TOOLING_PREVIEW)
    implementation(Library.COMPOSE_ACTIVITY)
    implementation(Library.COMPOSE_NAVIGATION)
    implementation(Library.ANDROIDX_LIFECYCLE_RUNTIME_KTX)
    implementation(Library.JACKSON_MODULE_KOTLIN)
    debugImplementation(Library.COMPOSE_UI_TOOLING)
    testImplementation(Library.JUNIT)
    androidTestImplementation(Library.ANDROIDX_TEST_JUNIT)
    androidTestImplementation(Library.ESPRESSO_CORE)
    androidTestImplementation(Library.COMPOSE_UI_TEST_JUNIT)
}