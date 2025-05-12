#include <jni.h>
#include <android/log.h>
#include <cstring>
#include <cstdio>
#include "toolChecker.h"

#define  LOG_TAG    "RootBeer"
#define  LOG_D(...)  if (DEBUG) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__);

static int DEBUG = 1;

extern "C" {

int Java_uz_fido_utils_libs_rootbeer_RootBeerNative_setLogDebugMessages(JNIEnv *env, jobject thiz, jboolean debug) {
    if (debug) {
        DEBUG = 1;
    } else {
        DEBUG = 0;
    }
    return 0;
}

int exists(const char *fname) {
    FILE *file;
    if ((file = fopen(fname, "r"))) {
        LOG_D("LOOKING FOR BINARY: %s PRESENT!!!", fname);
        fclose(file);
        return 1;
    }
    LOG_D("LOOKING FOR BINARY: %s Absent :(", fname);
    return 0;
}

int Java_uz_fido_utils_libs_rootbeer_RootBeerNative_checkForRoot(JNIEnv *env, jobject thiz, jobjectArray pathsArray) {

    int binariesFound = 0;

    int stringCount = (env)->GetArrayLength(pathsArray);

    for (int i = 0; i < stringCount; i++) {
        auto string = (jstring) (env)->GetObjectArrayElement(pathsArray, i);
        const char *pathString = (env)->GetStringUTFChars(string, nullptr);

        binariesFound += exists(pathString);

        (env)->ReleaseStringUTFChars(string, pathString);
    }

    return binariesFound > 0;
}
}