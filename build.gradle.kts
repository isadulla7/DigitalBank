plugins {
    id("org.jetbrains.kotlin.android") version "1.6.10" apply false
    id("com.google.dagger.hilt.android") version ("2.42") apply (false)
    `kotlin-dsl`
}

buildscript {
    repositories {
        mavenCentral()
        maven(url = "https://maven.google.com/")
        jcenter()
        google()
    }
    dependencies {
        classpath(RootLibraries.classpathGradle)
        classpath(RootLibraries.classpathKotlinGradle)
        classpath(RootLibraries.classpathNavigationSafeargs)
        classpath(RootLibraries.classpathDaggerHiltVersion)
        classpath(RootLibraries.classpathCrashlytics)
        classpath(RootLibraries.classPathKotlinSerialization)
//        classpath(RootLibraries.classPathFirebasePerfs)
//        classpath(RootLibraries.classPathGoogleService)
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven(url = "https://maven.google.com/")
        maven(url = "https://jitpack.io")
        gradlePluginPortal()
        google()
    }
}