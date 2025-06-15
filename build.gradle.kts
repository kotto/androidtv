// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt.gradle.plugin) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    // id("com.google.gms.google-services") version "4.4.1" apply false
    // id("com.google.firebase.crashlytics") version "2.9.9" apply false // Use the correct version for Crashlytics
    // id("com.google.firebase.firebase-perf") version "1.4.2" apply false // Use the correct version for Performance Monitoring
}

ext {
    set("compose_version", "1.5.14") // Replace "1.5.1" with your desired version
    // You might have other extra properties here, like room_version
    set("room_version", "2.6.1") // Uncomment this line
}
