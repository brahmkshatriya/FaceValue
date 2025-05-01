import com.android.build.gradle.internal.tasks.factory.dependsOn
import de.undercouch.gradle.tasks.download.Download

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.download)
}

val assetDir = "$projectDir/src/main/assets"
val faceDetectionFile = "face_detection_short_range.tflite"
tasks.register<Download>("face-detection-model-file") {
    src("https://storage.googleapis.com/mediapipe-models/face_detector/blaze_face_short_range/float16/1/blaze_face_short_range.tflite")
    dest("$assetDir/$faceDetectionFile")
    overwrite(false)
}

tasks.preBuild.dependsOn("face-detection-model-file")

android {
    namespace = "dev.brahmkshatriya.facevalue"
    compileSdk = 35

    defaultConfig {
        buildConfigField("String", "FACE_DETECTION_MODEL", "\"$faceDetectionFile\"")
        applicationId = "dev.brahmkshatriya.facevalue"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(libs.bundles.androidx)
    implementation(libs.material)
    implementation(libs.kotlin.serialization)
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
    implementation(libs.bundles.koin)
    implementation(libs.okhttp)
    implementation(libs.bundles.coil)
    implementation(libs.bundles.mediapipe)
}