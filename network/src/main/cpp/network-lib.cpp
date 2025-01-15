#include <jni.h>
#include <string>
#include <iostream>

extern "C" {

JNIEXPORT jstring
Java_uz_fido_network_di_Keys_getUserInfoUrl(JNIEnv *env, jobject thiz) {
    char *clientHashId = "https://requestid.universalbank.uz/api/request/identify/";
    return env->NewStringUTF(clientHashId);
}

JNIEXPORT jstring
Java_uz_fido_network_di_Keys_getClientId(JNIEnv *env, jobject thiz) {
    char *clientId = "-3";
    return env->NewStringUTF(clientId);
}

JNIEXPORT jstring
Java_uz_fido_network_di_Keys_getBaseUrl(JNIEnv *env, jobject thiz) {
    char *baseUrl = "https://ra.ubank.uz/api/";
    return env->NewStringUTF(baseUrl);
}

JNIEXPORT jstring
Java_uz_fido_network_di_Keys_getSocketUrl(JNIEnv *env, jobject thiz) {
    char *socketUrl = "https://ss.ubank.uz/api/";
    return env->NewStringUTF(socketUrl);
}

JNIEXPORT jstring
Java_uz_fido_network_di_Keys_paynetPhotoUrl(JNIEnv *env, jobject thiz) {
    char *socketUrl = "https://ibank.ubank.uz/files/";
    return env->NewStringUTF(socketUrl);
}

JNIEXPORT jstring
Java_uz_fido_network_di_Keys_getCertFilePassword(JNIEnv *env, jobject thiz) {
    char *certFilePassword = "223377";
    return env->NewStringUTF(certFilePassword);
}

}
