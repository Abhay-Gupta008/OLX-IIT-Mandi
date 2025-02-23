plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.krackhack.olxiitmandi"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.krackhack.olxiitmandi"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }
buildFeatures{
    viewBinding = true
    dataBinding = true
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation (libs.material.v150)
    implementation (libs.circleindicator)
    implementation (libs.whynotimagecarousel)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    implementation (libs.firebase.firestore)
    implementation (libs.firebase.analytics)
    implementation(platform(libs.firebase.bom))
    implementation (libs.firebase.auth)
    implementation (libs.firebase.storage)
    implementation(libs.firebase.firestore)
    implementation (libs.circleimageview)
    implementation (libs.viewpager2)
    implementation (libs.picasso.v28)
    implementation (libs.photoview)
    implementation (libs.materialsearchbar)
    implementation (libs.androidx.room.runtime)
    implementation (libs.converter.gson)
    implementation (libs.firebase.messaging.v2311)
    implementation (libs.androidx.room.runtime.v252)
    annotationProcessor (libs.androidx.room.compiler.v252)
    implementation (libs.androidx.room.ktx)
    implementation (libs.firebase.messaging)
    implementation (libs.okhttp.v493)
    implementation (libs.firebase.firestore.v2441)
    implementation (libs.imagepicker)
    implementation (libs.permissions)
    implementation (libs.glide)
    implementation (libs.androidx.core)
    implementation(libs.androidx.room.runtime.v250)
    annotationProcessor (libs.androidx.room.compiler)
    implementation (libs.androidx.room.runtime)
    annotationProcessor (libs.androidx.room.compiler)
    implementation (libs.glide.v4120)
    annotationProcessor (libs.compiler)
    // Lifecycle components (optional but recommended)
    implementation (libs.androidx.lifecycle.viewmodel.ktx.v260)
    implementation (libs.androidx.lifecycle.livedata.ktx.v260)

    implementation(libs.androidx.cardview)
    implementation (libs.whynotimagecarousel)
    implementation (libs.androidx.room.runtime)
    annotationProcessor (libs.androidx.room.compiler)
    implementation (libs.androidx.lifecycle.livedata.ktx)
    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.firebase.appcheck.playintegrity)
}
