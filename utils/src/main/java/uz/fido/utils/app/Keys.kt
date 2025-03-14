package uz.fido.utils.app

import uz.fido.utils.log.Log

object Keys {

    init {
        try {
            System.loadLibrary("utils-lib")
        } catch (e: UnsatisfiedLinkError) {
            Log.e("NativeLibrary", "Failed to load library: ${e.message}")
        }
    }

    external fun getCipherInstances(): String

    external fun getInitializationVector(): String

    external fun getAesSalt(): String

    external fun getSecretKeyInstance(): String

    external fun getDefaultAlgorithm(): String
}