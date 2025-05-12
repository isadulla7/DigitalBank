/****************************************************************************
 * File:   toolChecker.h
 * Author: Matthew Rollings
 * Date:   19/06/2015
 *
 * Description : Root checking JNI NDK code
 *
 ****************************************************************************/

extern "C" {

#include <jni.h>

int Java_uz_fido_utils_libs_rootbeer_RootBeerNative_setLogDebugMessages(JNIEnv *env, jobject thiz, jboolean debug);

int Java_uz_fido_utils_libs_rootbeer_RootBeerNative_checkForRoot(JNIEnv *env, jobject thiz, jobjectArray pathsArray);

}
