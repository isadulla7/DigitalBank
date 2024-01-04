object ProjectDependencies {

    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val legacySupport = "androidx.legacy:legacy-support-v4:${Versions.legacySupport}"
    const val testJunit = "androidx.test.ext:junit:${Versions.testJunit}"
    const val testImplJunit = "junit:junit:${Versions.testImplJunit}"
    const val testEspresso = "androidx.test.espresso:espresso-core:${Versions.testEspresso}"
    const val espressoIdling =
        "androidx.test.espresso:espresso-idling-resource:${Versions.espressoIdling}"

    const val material = "com.google.android.material:material:${Versions.material}"
    const val biometric = "androidx.biometric:biometric:${Versions.biometric}"
    const val workRuntime = "androidx.work:work-runtime-ktx:${Versions.workRuntime}"

    const val playServicesVision =
        "com.google.android.gms:play-services-vision:${Versions.playServicesVision}"
    const val playServicesMaps =
        "com.google.android.gms:play-services-maps:${Versions.playServicesMaps}"
    const val playServicesLocation =
        "com.google.android.gms:play-services-location:${Versions.playServicesLocation}"
    const val playServicesMapsUtils =
        "com.google.maps.android:android-maps-utils:${Versions.playServicesMapsUtils}"

    const val barcodeScanning = "com.google.mlkit:barcode-scanning:${Versions.barcode}"
    const val smsRetrieve =
        "com.google.android.gms:play-services-auth-api-phone:${Versions.smsRetrieve}"

    const val inAppUpdate = "com.google.android.play:core-ktx:${Versions.inAppUpdate}"
    const val paper = "io.paperdb:paperdb:${Versions.paper}"
    const val coroutinesCore =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesCore}"
    const val coroutinesAndroid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesAndroid}"
    const val lottie = "com.airbnb.android:lottie:${Versions.lottie}"

    const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.liveDataKtx}"
    const val lifeCycleExtension =
        "androidx.lifecycle:lifecycle-extensions:${Versions.lifeCycleExtension}"
    const val wearable = "com.google.android.wearable:wearable:${Versions.wearable}"
    const val lifeCycleCompiler =
        "androidx.lifecycle:lifecycle-compiler:${Versions.lifeCycleCompiler}"

    const val roomRuntime = "androidx.room:room-runtime:${Versions.roomRuntime}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.roomKtx}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.roomCompiler}"

    const val navigationFragment =
        "androidx.navigation:navigation-fragment-ktx:${Versions.navigationFragment}"
    const val navigationUi = "androidx.navigation:navigation-ui-ktx:${Versions.navigationUi}"
    const val navigationKtx = "androidx.fragment:fragment-ktx:${Versions.navigationKtx}"

    const val coil = "io.coil-kt:coil:${Versions.coil}"
    const val picasso = "com.squareup.picasso:picasso:${Versions.picasso}"
    const val videoCache = "com.danikula:videocache:${Versions.videoCache}"

    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glideCompiler}"
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hiltAndroid}"
    const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hiltCompiler}"
    const val sharedPref = "androidx.preference:preference-ktx:${Versions.sharedPref}"
    const val circleImageView = "de.hdodenhof:circleimageview:${Versions.circleImageView}"
    const val expandableLayout =
        "net.cachapa.expandablelayout:expandablelayout:${Versions.expandableLayout}"

    const val camera = "androidx.camera:camera-camera2:${Versions.camera}"
    const val cameraLifecycle = "androidx.camera:camera-lifecycle:${Versions.cameraLifecycle}"
    const val cameraView = "androidx.camera:camera-view:${Versions.cameraView}"

    const val firebaseBom = "com.google.firebase:firebase-bom:${Versions.firebaseBom}"
    const val firebaseAnalytics = "com.google.firebase:firebase-analytics-ktx"
    const val firebaseMessaging = "com.google.firebase:firebase-messaging-ktx"
    const val firebaseMessagingVersion =
        "com.google.firebase:firebase-messaging-ktx:${Versions.firebaseMessaging}"
    const val firebaseStorage = "com.google.firebase:firebase-storage:${Versions.firebaseStorage}"
    const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics"
    const val firebaseDynamicLinks = "com.google.firebase:firebase-dynamic-links-ktx"
    const val firebaseFireStore =
        "com.google.firebase:firebase-firestore-ktx:${Versions.firebaseFireStore}"

    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitConverter =
        "com.squareup.retrofit2:converter-gson:${Versions.retrofitConverter}"
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val okhttpLogging = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttpLogging}"
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
    const val viewModelKoin = "org.koin:koin-androidx-viewmodel:${Versions.viewModelKoin}"
    const val libMaterialProgress =
        "com.pnikosis:materialish-progress:${Versions.libMaterialProgress}"
    const val smartRefreshLayout =
        "com.scwang.smartrefresh:SmartRefreshLayout:${Versions.smartRefreshLayout}"
    const val smartRefreshHeader =
        "com.scwang.smartrefresh:SmartRefreshHeader:${Versions.smartRefreshHeader}"
    const val skeleton = "com.ethanhua:skeleton:${Versions.skeleton}"
    const val shimmerLayout = "io.supercharge:shimmerlayout:${Versions.shimmerLayout}"
    const val facebookShimmer = "com.facebook.shimmer:shimmer:${Versions.facebookShimmer}"
    const val chuckerLib = "com.github.chuckerteam.chucker:library:${Versions.chuckerLib}"
    const val chuckerLibNoOp =
        "com.github.chuckerteam.chucker:library-no-op:${Versions.chuckerLibNoOp}"
    const val ticker = "com.robinhood.ticker:ticker:${Versions.ticker}"
    const val ktorAndroid = "io.ktor:ktor-client-android:${Versions.ktorAndroid}"
    const val kotlinxCoroutines =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinxCoroutines}"

    const val mlkitFaceDetection =
        "com.google.android.gms:play-services-mlkit-face-detection:${Versions.mlkitFaceDetection}"
    const val mlkitTextRecognition =
        "com.google.android.gms:play-services-mlkit-text-recognition:${Versions.mlkitTextRecognition}"
    const val mlkitBarcodeScanning =
        "com.google.android.gms:play-services-mlkit-barcode-scanning:${Versions.mlkitBarcodeScanning}"

    const val lifecycleViewModel =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycleViewModel}"
    const val lifecycleRuntime =
        "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleRuntime}"
    const val splashScreen = "androidx.core:core-splashscreen:${Versions.splashScreen}"
    const val securityCrypto = "androidx.security:security-crypto:${Versions.securityCrypto}"
    const val playServiceVisionCommon =
        "com.google.android.gms:play-services-vision-common:${Versions.playServiceVisionCommon}"
    const val playServiceVision =
        "com.google.android.gms:play-services-vision:${Versions.playServiceVision}"
    const val rxBinding = "com.jakewharton.rxbinding4:rxbinding:${Versions.rxBinding}"
    const val viewPagerDotIndicator =
        "com.tbuonomo.andrui:viewpagerdotsindicator:${Versions.viewPagerDotIndicator}"
    const val pageTransformerHelper =
        "com.github.OCNYang:PageTransformerHelp:${Versions.pageTransformerHelper}"
    const val rvAnimators = "jp.wasabeef:recyclerview-animators:${Versions.rvAnimators}"
    const val qrGenAndroid = "com.github.kenglxn.QRGen:android:${Versions.qrGenAndroid}"
    const val customQrGenerator =
        "com.github.alexzhirkevich:custom-qr-generator:${Versions.customQrGenerator}"
    const val commonsLang = "org.apache.commons:commons-lang3:${Versions.commonsLang}"
    const val animatedProgressBar =
        "com.github.mckrpk:AnimatedProgressBar:${Versions.animatedProgressBar}"
    const val rxRelay = "com.jakewharton.rxrelay2:rxrelay:${Versions.rxRelay}"
    const val databinding = "androidx.databinding:databinding-runtime:${Versions.databinding}"
    const val tensorFlow = "org.tensorflow:tensorflow-lite:${Versions.tensorFlow}"
    const val xenione = "com.xenione.widgets:loupe:${Versions.xenione}"
    const val flexbox = "com.google.android.flexbox:flexbox:${Versions.flexbox}"

    const val myId = "uz.myid.sdk.capture:myid-capture-sdk:2.2.5"
    const val humoPay1 = "aars/d8-hce-sdk-494-cbp-logs-release.aar"
    const val humoPay2 = "aars/d8-hce-sec-465-dasho-release.aar"
    const val nfcCardReader = "aars/nfccardreaderlib-release.aar"
    const val magnifier = "com.xenione.widgets:loupe:1.0.0"

}