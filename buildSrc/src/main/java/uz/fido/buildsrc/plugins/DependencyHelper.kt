import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.initFirebaseLibraries() {
//    implementation(FirebaseLibraries.google_play_services)
//    implementation(FirebaseLibraries.firebase_dynamic_links)
//    implementation(FirebaseLibraries.firebase_authentication)
//    implementation(FirebaseLibraries.firebase_crashlytics)
//    implementation(FirebaseLibraries.firebase_config)
//    implementation(FirebaseLibraries.firebase_analytics)
//    implementation(FirebaseLibraries.firebase_core)
//    implementation(FirebaseLibraries.firebase_messaging)
//    implementation(FirebaseLibraries.firebase_performance)
}

fun DependencyHandler.initRequiredLibraries() {
    api(RequiredLibraries.kotlinStdLib)
    api(RequiredLibraries.core_ktx)
    api(RequiredLibraries.coroutines_android)
    api(RequiredLibraries.coroutines_core)
    api(RequiredLibraries.coroutines_test)
    implementation(RequiredLibraries.json_serializer)
    api(RequiredLibraries.lifecycle_extension)
    api(RequiredLibraries.viewbinding)
    api(RequiredLibraries.gson)
    api(RequiredLibraries.runtime_ktx)
    api(RequiredLibraries.hilt_android)
    kapt(RequiredLibraries.kapt_hilt_android_compiler)
    kapt(RequiredLibraries.kapt_hilt_compiler)
    api(RequiredLibraries.viewmodel_ktx)
    api(RequiredLibraries.livedata_ktx)
}


fun DependencyHandler.initSupportLibraries() {
    implementation(SupportLibraries.appCompat)
    implementation(SupportLibraries.constraintLayout)
    implementation(SupportLibraries.material)
    implementation(SupportLibraries.recyclerview)
    implementation(SupportLibraries.card_view)
    implementation(SupportLibraries.legacy_support)
    implementation(SupportLibraries.viewpager2)
    implementation(SupportLibraries.activity_ktx)
    implementation(SupportLibraries.fragment_ktx)
    implementation(SupportLibraries.navigation_fragment_ktx)
    implementation(SupportLibraries.navigation_ui_ktx)
    implementation(SupportLibraries.splash_screen)
}

fun DependencyHandler.initTestLibraries() {
    implementation(TestLibraries.espressoCore)
    implementation(TestLibraries.junit)
    implementation(TestLibraries.junitTest)
    androidTestImplementation(TestLibraries.androidTestImplementationRobolectric)
    androidTestImplementation("androidx.arch.core:core-testing:2.0.0")
}