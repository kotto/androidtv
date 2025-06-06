// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt.gradle.plugin) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

ext {
    set("compose_version", "1.5.14") // Replace "1.5.1" with your desired version
    // You might have other extra properties here, like room_version
    set("room_version", "2.6.1") // Uncomment this line
}
