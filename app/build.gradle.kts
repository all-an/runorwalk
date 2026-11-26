plugins {
    id("com.android.application")
}

android {
    namespace = "com.runorwalk"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.runorwalk"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // KISS: no AndroidX, no map SDK, no Play Services. Pure framework APIs only.
    testImplementation("junit:junit:4.13.2")
}
