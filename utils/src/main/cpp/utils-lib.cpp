#include <jni.h>
#include <string>
#include <iostream>

extern "C" {
JNIEXPORT jstring
Java_uz_fido_utils_app_Keys_getCipherInstances(JNIEnv *env, jobject thiz) {
    char const *cipherInstances = "AES/CBC/PKCS5Padding";
    return env->NewStringUTF(cipherInstances);
}

JNIEXPORT jstring
Java_uz_fido_utils_app_Keys_getInitializationVector(JNIEnv *env, jobject thiz) {
    char const *initializationVector = "8119745113154120";
    return env->NewStringUTF(initializationVector);
}

JNIEXPORT jstring
Java_uz_fido_utils_app_Keys_getAesSalt(JNIEnv *env, jobject thiz) {
    char const *aesSalt = "qwerty";
    return env->NewStringUTF(aesSalt);
}

JNIEXPORT jstring
Java_uz_fido_utils_app_Keys_getSecretKeyInstance(JNIEnv *env, jobject thiz) {
    char const *secretKeyInstance = "PBKDF2WithHmacSHA1";
    return env->NewStringUTF(secretKeyInstance);
}

JNIEXPORT jstring
Java_uz_fido_utils_app_Keys_getDefaultAlgorithm(JNIEnv *env, jobject thiz) {
    char const *defaultAlgorithm = "AES";
    return env->NewStringUTF(defaultAlgorithm);
}

}
