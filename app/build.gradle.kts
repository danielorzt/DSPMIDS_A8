import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// La clave de la API se lee de local.properties para no escribirla en el código.
val localProps = Properties().apply {
    val archivo = rootProject.file("local.properties")
    if (archivo.exists()) archivo.inputStream().use { load(it) }
}

android {
    namespace = "com.danielorzt.climamovil"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.danielorzt.climamovil"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        val clave = localProps.getProperty("OPENWEATHER_API_KEY", "")
        buildConfigField("String", "OPENWEATHER_API_KEY", "\"$clave\"")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.activity:activity-ktx:1.9.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")

    // Llamadas a la API y conversión del JSON
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Corrutinas para no bloquear el hilo principal
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
