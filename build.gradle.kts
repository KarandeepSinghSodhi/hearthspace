// build.gradle.kts (Project level)

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.5.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
        classpath("com.google.gms:google-services:4.4.2") // Firebase services
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

// Enable Gradle wrapper version
wrapper {
    gradleVersion = "8.7"
    distributionType = Wrapper.DistributionType.ALL
}
