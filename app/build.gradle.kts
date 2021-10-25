plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "com.samwdev.battlecity"
        minSdk = 23
        targetSdk = 31
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
        useIR = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Library.composeVersion
        kotlinCompilerVersion = "1.5.30"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(Library.ANDROIDX_CORE)
    implementation(Library.ANDROIDX_APPCOMPAT)
    implementation(Library.MATERIAL)
    implementation(Library.COMPOSE_UI)
    implementation(Library.ANDROIDX_LIFECYCLE_VIEW_MODEL)
    implementation("androidx.compose.material:material:${Library.composeVersion}")
    implementation("androidx.compose.ui:ui-tooling-preview:${Library.composeVersion}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.activity:activity-compose:1.3.1")
    testImplementation(Library.JUNIT)
    androidTestImplementation(Library.ANDROIDX_TEST_JUNIT)
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation(Library.COMPOSE_UI_TEST_JUNIT)
    debugImplementation("androidx.compose.ui:ui-tooling:${Library.composeVersion}")
}