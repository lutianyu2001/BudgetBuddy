plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs.kotlin")
    id("io.github.takahirom.roborazzi") version "1.8.0-alpha-5"
    id("com.google.gms.google-services")
}

android {
    namespace = "com.cs407.budgetbuddy"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cs407.budgetbuddy"
        minSdk = 35
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
        debug {
            isMinifyEnabled = false
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.testLogging {
                    events("passed", "failed", "standardOut", "standardError")
                    showExceptions = true
                    showCauses = true
                    showStackTraces = true
                    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
                    showStandardStreams = true
                }
                it.outputs.upToDateWhen { false }
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    testImplementation("org.robolectric:robolectric:4.13")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // Espresso dependencies
    testImplementation("androidx.test.espresso:espresso-core:3.6.1")
    testImplementation("androidx.test.espresso:espresso-contrib:3.6.1")
    testImplementation("androidx.test.espresso:espresso-intents:3.6.1")
    testImplementation("androidx.test:rules:1.6.1")
    testImplementation("androidx.test:runner:1.6.2")

    testImplementation("io.github.takahirom.roborazzi:roborazzi:1.8.0-alpha-5")
    testImplementation("io.github.takahirom.roborazzi:roborazzi-junit-rule:1.8.0-alpha-5")

    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.mockito:mockito-inline:3.11.2")

    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-auth-ktx:23.1.0")
}