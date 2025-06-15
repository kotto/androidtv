import org.gradle.jvm.toolchain.JavaLanguageVersion

// build.gradle.kts (Module: app)
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.hilt.gradle.plugin)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization")

    // Décommentez si vous réactivez Firebase
    // id("com.google.gms.google-services")
    // id("com.google.firebase.crashlytics")
    // id("com.google.firebase.firebase-perf")
}

android {
    namespace = "ai.maatcore.maatcore_android_tv"
    compileSdk = 36 // Utilisez libs.versions.compileSdk si défini dans TOML

    defaultConfig {
        applicationId = "ai.maatcore.maatcore_android_tv"
        minSdk = 21 // Utilisez libs.versions.minSdk si défini
        targetSdk = 36 // Utilisez libs.versions.targetSdk si défini
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField("String", "OPENAI_API_KEY", "\"${project.properties["OPENAI_API_KEY"] ?: "YOUR_API_KEY"}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Envisagez true pour les builds de production
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    sourceSets {
        getByName("main") {
            kotlin.srcDirs("src/main/java")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    kotlinOptions {
        jvmTarget = "17"
        // languageVersion = "1.9" // Généralement inféré par la version du plugin Kotlin
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11" // Aligned with Kotlin 1.9.23 compatibility
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
            excludes.add("/META-INF/DEPENDENCIES") // Souvent nécessaire avec certaines libs
            excludes.add("META-INF/INDEX.LIST")
        }
    }
}

dependencies {
    // Core AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.activity.ktx) // Déjà dans la section Hilt de votre fichier original, mais sa place est logique ici aussi.

    // Compose - Le BOM gère les versions
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.compose.foundation) // Assurez-vous que cet alias est pour 'androidx.compose.foundation:foundation'
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling) // Outil de debug pour Compose (ex: Live Edit)
    // Choisissez Material Design 3 OU TV Material comme principal.
    // Pour une app TV, tv-material est prioritaire.
    implementation(libs.androidx.compose.material3) // Si vous avez besoin de M3 standard en plus
    implementation(libs.androidx.compose.material.icons.extended) // Pour les icônes étendues

    // Android TV - Compose dependencies
    implementation("androidx.tv:tv-foundation:1.0.0-alpha10")
    implementation("androidx.tv:tv-material:1.0.0-alpha10")

    // Navigation
    implementation(platform(libs.androidx.compose.bom)) // BOM pour les dépendances Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation(libs.androidx.hilt.navigation.compose)   // Assurez-vous que cet alias est pour "androidx.hilt:hilt-navigation-compose"

    // ViewModel & LiveData (Lifecycle)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Dependency Injection - Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler) // Pour `com.google.dagger:hilt-android-compiler`

    // Networking - Retrofit & OkHttp (Assurez-vous d'avoir les alias dans libs.versions.toml)
    // implementation(libs.retrofit)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Networking - Ktor Client (Assurez-vous d'avoir les alias dans libs.versions.toml)
    // implementation(libs.ktor.client.core)
    // ... autres modules ktor
    // Exemple de chaînes :
    implementation("io.ktor:ktor-client-core:2.3.10")
    implementation("io.ktor:ktor-client-android:2.3.10")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.10")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.10")
    implementation("io.ktor:ktor-client-logging:2.3.10")


    // Serialization - Kotlinx
    implementation(libs.kotlinx.serialization.json) // Assurez-vous que cet alias existe

    // Image Loading - Coil
    implementation(libs.coil.compose) // Assurez-vous que cet alias existe
    implementation(libs.coil.video)   // Assurez-vous que cet alias existe

    // Video Player - ExoPlayer (Media3) (Assurez-vous d'avoir les aliases)
    // implementation(libs.androidx.media3.exoplayer)
    // ... autres modules media3
    // Exemple de chaînes :
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.3.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")
    implementation("androidx.media3:media3-session:1.3.1")
    // implementation("androidx.media3:media3-cast:1.3.1") // Si utilisé

    // Bluetooth
    // implementation(libs.androidx.bluetooth) // Si alias existe

    // Database - Room
    // Remplacez rootProject.extra par un alias du TOML si possible pour la version de Room
    // Par exemple, si vous avez roomVersion = "2.6.1" dans [versions] du TOML
    // et des alias comme androidx-room-runtime, androidx-room-ktx, androidx-room-compiler
    // implementation(libs.androidx.room.runtime)
    // implementation(libs.androidx.room.ktx)
    // kapt(libs.androidx.room.compiler)
    // Alternative avec rootProject.extra (moins idéale que le TOML complet) :
    val room_version = rootProject.extra["room_version"] as String
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")


    // Preferences DataStore
    // implementation(libs.androidx.datastore.preferences) // Si alias existe

    // Coroutines
    implementation(libs.kotlinx.coroutines.android) // Si alias existe
    // implementation(libs.kotlinx.coroutines.play.services) // Si alias existe

    // Security
    // implementation(libs.androidx.security.crypto) // Si alias existe

    // Work Manager
    implementation(libs.androidx.work.runtime.ktx) // Si alias existe
    implementation(libs.androidx.hilt.work)       // Si alias existe
    // Pour `androidx.hilt:hilt-compiler` (spécifique aux extensions Hilt comme WorkManager)
    // Assurez-vous que `libs.androidx.hilt.ext.compiler` ou un alias similaire est défini dans le TOML
    // et qu'il pointe vers `androidx.hilt:hilt-compiler` avec la bonne version (ex: "1.1.0" ou "1.2.0")
    // kapt(libs.androidx.hilt.ext.compiler)


    // Firebase BOM (doit être DÉCLARÉ AVANT les autres dépendances Firebase pour gérer leurs versions)
    // Firebase BOM (doit être DÉCLARÉ AVANT les autres dépendances Firebase pour gérer leurs versions)
    // implementation(platform(libs.firebase.bom)) // Assurez-vous que cet alias existe

    // Analytics & Monitoring
    // implementation(libs.firebase.analytics)    // Si alias existe pour firebase-analytics-ktx
    // implementation(libs.firebase.crashlytics) // Si alias existe pour firebase-crashlytics-ktx
    // implementation(libs.firebase.performance) // Si alias existe

    // Push Notifications
    // implementation(libs.firebase.messaging)   // Si alias existe pour firebase-messaging-ktx


    // Biometric Authentication
    // implementation(libs.androidx.biometric) // Si alias existe

    // Animation - Lottie
    implementation(libs.lottie.compose) // Si alias existe

    // Permission Handling - Accompanist
    implementation(libs.accompanist.permissions) // Si alias existe

    // System UI Controller - Accompanist
    implementation(libs.accompanist.systemuicontroller) // Si alias existe

    // Paging 3
    implementation(libs.androidx.paging.runtime.ktx) // Si alias existe
    implementation(libs.androidx.paging.compose)   // Si alias existe

    // Glance (App Widgets)
    implementation(libs.androidx.glance.appwidget) // Si alias existe
    implementation(libs.androidx.glance.material3) // Si alias existe

    implementation(libs.androidx.compose.material.icons.extended) // Ou libs.androidx.compose.material.icons.core

    implementation("androidx.compose.ui:ui-graphics:1.6.0-alpha03")

    // Testing
    testImplementation(libs.junit) // Si alias existe pour "junit:junit"
    // testImplementation(libs.mockito.core) // Si alias existe
    // testImplementation(libs.kotlinx.coroutines.test) // Si alias existe
    // testImplementation(libs.androidx.arch.core.testing) // Si alias existe

    androidTestImplementation(libs.androidx.test.ext.junit) // Si alias existe
    androidTestImplementation(libs.androidx.test.espresso.core) // Si alias existe
    androidTestImplementation(platform(libs.androidx.compose.bom)) // BOM aussi pour les tests Compose
    androidTestImplementation(libs.androidx.ui.test.junit4) // Alias pour "androidx.compose.ui:ui-test-junit4"
    debugImplementation(libs.androidx.ui.test.manifest)   // Alias pour "androidx.compose.ui:ui-test-manifest"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs += listOf(
            "-Xallow-unstable-dependencies",
            "-Xsuppress-version-warnings",

        )
    }
}
