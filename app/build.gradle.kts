plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.example.dressapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.dressapp"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Biblioteca para recortar imágenes
    implementation ("com.github.castorflex.verticalviewpager:library:19.0.1")
    implementation ("com.squareup.picasso:picasso:2.8")

    // Biblioteca de Material Design
    implementation ("com.google.android.material:material:1.12.0")

    // Biblioteca de compatibilidad de AndroidX
    implementation ("androidx.appcompat:appcompat:1.6.1")

    // Biblioteca de ConstraintLayout
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")

    // Firebase Crashlytics
    implementation ("com.google.firebase:firebase-crashlytics:18.6.2")

    // Firebase Authentication
    implementation ("com.google.firebase:firebase-auth:22.3.1")

    // Firebase Realtime Database
    implementation ("com.google.firebase:firebase-database:20.3.0")

    // Firebase Storage
    implementation ("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-firestore:24.10.3")

    // Dependencias para pruebas unitarias
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.theartofdev.edmodo:android-image-cropper:2.8.0")

    // Biblioteca para cargar imágenes en ImageView
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
}
