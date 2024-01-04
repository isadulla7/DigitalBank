package uz.fido.utils.app

interface PermissionInterface {

    fun locationPermissionGranted() {}

    fun storagePermissionGranted() {}

    fun cameraPermissionGranted() {}

    fun contactsPermissionGranted() {}

    fun permissionDenied() {}

}