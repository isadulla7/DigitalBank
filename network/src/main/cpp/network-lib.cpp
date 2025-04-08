#include <jni.h>
#include <string>
#include <iostream>

extern "C" {

JNIEXPORT jstring
Java_uz_fido_network_di_Keys_getUserInfoUrl(JNIEnv *env, jobject thiz) {
    char const *clientHashId = "https://requestid.universalbank.uz/api/request/identify/";
    return env->NewStringUTF(clientHashId);
}

JNIEXPORT jstring
Java_uz_fido_network_di_Keys_getClientId(JNIEnv *env, jobject thiz) {
    char const *clientId = "-3";
    return env->NewStringUTF(clientId);
}

JNIEXPORT jstring
Java_uz_fido_network_di_Keys_getCertificatePin(JNIEnv *env, jobject thiz) {
    char const *pinner = "sha256/P8Meknq+VzYp+Y/EiOHnGk5usNgeRR1LTPUZwtTspv4=";
    return env->NewStringUTF(pinner);
}

JNIEXPORT jstring
Java_uz_fido_network_di_Keys_getBaseUrl(JNIEnv *env, jobject thiz) {
    char const *baseUrl = "https://ra.ubank.uz/api/";
    return env->NewStringUTF(baseUrl);
}

JNIEXPORT jstring
Java_uz_fido_network_di_Keys_getSocketUrl(JNIEnv *env, jobject thiz) {
    char const *socketUrl = "https://ss.ubank.uz/api/";
    return env->NewStringUTF(socketUrl);
}

JNIEXPORT jstring
Java_uz_fido_network_di_Keys_paynetPhotoUrl(JNIEnv *env, jobject thiz) {
    char const *socketUrl = "https://ibank.ubank.uz/files/";
    return env->NewStringUTF(socketUrl);
}

JNIEXPORT jstring
Java_uz_fido_network_di_Keys_getCertFilePassword(JNIEnv *env, jobject thiz) {
    char const *certFilePassword = "223377";
    return env->NewStringUTF(certFilePassword);
}

}
