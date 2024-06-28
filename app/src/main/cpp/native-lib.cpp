#include <jni.h>
#include <string>
#include <iostream>

extern "C" {
JNIEXPORT jstring
Java_uz_fido_universaldigital_ui_utils_keys_Keys_getMyIdClientId(JNIEnv *env, jobject thiz) {
    char *clientId = "universal_mobile-m2D6uFzCBlj3vw9KnXSiyvBj1ofNO6Q3fdZZ96HL";
    return env->NewStringUTF(clientId);
}

JNIEXPORT jstring
Java_uz_fido_universaldigital_ui_utils_keys_Keys_getMyIdClientHash(JNIEnv *env, jobject thiz) {
    char *clientHash = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmvWRa3C/jFqQHfs1kbVSysOBFRKiEAfw1iPD16Rh2h3fq3dkLcgOrYs9DcQOrJSknftkMSSkO2gGLlf8fnqnAjxAYml1vnGC5rYJTUUQTBKvpgchcPlLfUu/MDKUK3PSZFrYNIcYSWFfkGHgUNRk8pYPUH7OIF6uboERqSeG4M74zogAEP4Zkk76NTuH+F6exB+jih88cIicXIvSiodb99OE2LmBwPAUvw8MXVutzrJg+oeqCGUfbBnxCXbksmdxpoSVsRDz8TBPI0IMLrOx3F6dW1L+Pde9m+jhblybHazaY7WEufwWQJ4EajvMZfF3IBgQgrP4MwaY+HFtFYbVMwIDAQAB";
    return env->NewStringUTF(clientHash);
}

JNIEXPORT jstring
Java_uz_fido_universaldigital_ui_utils_keys_Keys_getMyIdClientHashId(JNIEnv *env, jobject thiz) {
    char *clientHashId = "4cc848bb-49ee-4db4-bf3b-119f141f52e4";
    return env->NewStringUTF(clientHashId);
}

JNIEXPORT jstring
Java_uz_fido_universaldigital_ui_utils_keys_Keys_getUserInfoUrl(JNIEnv *env, jobject thiz) {
    char *userInfoUrl = "https://requestid.universalbank.uz/api/request/identify/";
    return env->NewStringUTF(userInfoUrl);
}

JNIEXPORT jstring
Java_uz_fido_universaldigital_ui_utils_keys_Keys_getClientId(JNIEnv *env, jobject thiz) {
    char *clientId = "-3";
//    char *clientId = "-2";
    return env->NewStringUTF(clientId);
}

JNIEXPORT jstring
Java_uz_fido_universaldigital_ui_utils_keys_Keys_paynetPhotoUrl(JNIEnv *env, jobject thiz) {
    char *socketUrl = "https://ibank.ubank.uz/files/";
    return env->NewStringUTF(socketUrl);
}

JNIEXPORT jstring
Java_uz_fido_universaldigital_ui_utils_keys_Keys_getDepositOfferBaxtliBolalik(JNIEnv *env, jobject thiz) {
    char *depositOffer = "http://87.237.237.230:8181/tel/mobile/depozit_baxtliBolalik.pdf";
    return env->NewStringUTF(depositOffer);
}

JNIEXPORT jstring
Java_uz_fido_universaldigital_ui_utils_keys_Keys_getClientSecret(JNIEnv *env, jobject thiz) {
    char *depositOffer = "64riG3bHuGAFc7O79tGQx0cz46SSkgNGukHFL14yKs9XnOZPBfBcMZDGmkZkwbE7shSwQD6I4jrjQXwtcmvKmQsFYbvZTOI92Dxd";
    return env->NewStringUTF(depositOffer);
}
}
