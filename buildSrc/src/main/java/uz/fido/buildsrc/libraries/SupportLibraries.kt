import uz.fido.buildsrc.libraries.Versions
import uz.fido.buildsrc.Configs

object RootLibraries {
    const val classpathGradle = "com.android.tools.build:gradle:${Versions.classpathGradleVersion}"
    const val classpathKotlinGradle =
        "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
    const val classPathGoogleService =
        "com.google.gms:google-services:${Versions.classpathGoogleServices}"
    const val classpathNavigationSafeargs =
        "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation_fragment_ktx}"
    const val classpathDaggerHiltVersion =
        "com.google.dagger:hilt-android-gradle-plugin:${Versions.classpathDaggerHiltVersion}"
    const val classPathFirebasePerfs =
        "com.google.firebase:perf-plugin:${Versions.classpathFirebasePerfs}"
    const val classpathCrashlytics =
        "com.google.firebase:firebase-crashlytics-gradle:${Versions.classpathCrashlytics}"
    const val classPathKotlinSerialization =
        "org.jetbrains.kotlin:kotlin-serialization:${Versions.classpathKotlinSerialization}"
}

object FirebaseLibraries {
    const val google_play_services = "com.google.android.gms:play-services-auth:20.2.0"
    const val firebase_messaging =
        "com.google.firebase:firebase-messaging:${Versions.firebase_messaging}"
    const val firebase_core = "com.google.firebase:firebase-core:${Versions.firebase_core}"
    const val firebase_analytics =
        "com.google.firebase:firebase-analytics:${Versions.firebase_analytics}"
    const val firebase_config = "com.google.firebase:firebase-config:${Versions.firebase_config}"
    const val firebase_authentication =
        "com.google.firebase:firebase-auth:${Versions.firebase_authentication}"
    const val firebase_crashlytics =
        "com.google.firebase:firebase-crashlytics:${Versions.firebase_crashlytics}"
    const val firebase_dynamic_links =
        "com.google.firebase:firebase-dynamic-links:${Versions.firebase_dynamic_links}"
    const val firebase_performance =
        "com.google.firebase:firebase-perf:${Versions.firebase_performance}"
}

object RequiredLibraries {
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}"
    const val core_ktx = "androidx.core:core-ktx:${Versions.core_ktx}"
    const val coroutines_android =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines_android}"
    const val coroutines_core =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines_core}"
    const val coroutines_test =
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines_test}"
    const val json_serializer =
        "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.json_serializer}"
    const val lifecycle_extension =
        "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle_extension}"
    const val viewbinding = "com.android.databinding:viewbinding:${Versions.view_binding}"
    const val runtime_ktx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.runtime_ktx}"
    const val viewmodel_ktx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.viewmodel_ktx}"
    const val livedata_ktx = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.livedata_ktx}"
    const val hilt_android = "com.google.dagger:hilt-android:${Versions.hilt_android}"
    const val kapt_hilt_android_compiler =
        "com.google.dagger:hilt-android-compiler:${Versions.hilt_android_compiler}"
    const val kapt_hilt_compiler = "androidx.hilt:hilt-compiler:${Versions.hilt_compiler}"
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
}

object SupportLibraries {
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompatVersion}"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayoutVersion}"
    const val material = "com.google.android.material:material:${Versions.materialVersion}"
    const val recyclerview = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
    const val card_view = "androidx.cardview:cardview:${Versions.card_view}"
    const val legacy_support = "androidx.legacy:legacy-support-v4:${Versions.legacy_support}"
    const val viewpager2 = "androidx.viewpager2:viewpager2:${Versions.viewpager2}"
    const val activity_ktx = "androidx.activity:activity-ktx:${Versions.activity_ktx}"
    const val fragment_ktx = "androidx.fragment:fragment-ktx:${Versions.fragment_ktx}"
    const val navigation_fragment_ktx =
        "androidx.navigation:navigation-fragment-ktx:${Versions.navigation_fragment_ktx}"
    const val navigation_ui_ktx =
        "androidx.navigation:navigation-ui-ktx:${Versions.navigation_fragment_ktx}"
    const val splash_screen = "androidx.core:core-splashscreen:${Versions.splash_screen}"
}

object TestLibraries {
    const val junit = "junit:junit:${Versions.junitVersion}"
    const val junitTest = "androidx.test.ext:junit:${Versions.junitTestVersion}"
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espressoCoreVersion}"
    const val androidTestImplementationRobolectric =
        "org.robolectric:robolectric:4.4" //androidTestImplementation
}