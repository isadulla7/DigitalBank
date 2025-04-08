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

JNIEXPORT jstring
Java_uz_fido_network_di_Keys_getCertPin1(JNIEnv *env, jobject thiz) {
    char const *pin1 = "sha256/P8Meknq+VzYp+Y/EiOHnGk5usNgeRR1LTPUZwtTspv4=";
    return env->NewStringUTF(pin1);
}

JNIEXPORT jstring
Java_uz_fido_network_di_Keys_getCertPin2(JNIEnv *env, jobject thiz) {
    char const *pin2 = "sha256/4a6cPehI7OG6cuDZka5NDZ7FR8a60d3auda+sKfg4Ng=";
    return env->NewStringUTF(pin2);
}

JNIEXPORT jstring
Java_uz_fido_network_di_Keys_getCertPin3(JNIEnv *env, jobject thiz) {
    char const *pin3 = "sha256/x4QzPSC810K5/cMjb05Qm4k3Bw5zBn4lTdO/nEW/Td4=";
    return env->NewStringUTF(pin3);
}

JNIEXPORT jstring
Java_uz_fido_network_di_Keys_getDomainName(JNIEnv *env, jobject thiz) {
    char const *domainName = "ra.ubank.uz";
    return env->NewStringUTF(domainName);
}

}
