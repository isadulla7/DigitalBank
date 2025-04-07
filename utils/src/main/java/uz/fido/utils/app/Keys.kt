package uz.fido.utils.app

object Keys {

    init {
        try {
            System.loadLibrary("utils-lib")
        } catch (e: UnsatisfiedLinkError) {
            //Failed to load library
        }
    }

    external fun getCipherInstances(): String

    external fun getInitializationVector(): String

    external fun getAesSalt(): String

    external fun getSecretKeyInstance(): String

    external fun getDefaultAlgorithm(): String
}