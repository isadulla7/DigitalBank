package uz.fido.utils.const

object APIServiceConst {

    //PROFILE IMAGE URL
    fun profileImageUrl(imageName: String) =
        "https://firebasestorage.googleapis.com/v0/b/universal-mobile-digital.appspot.com/o/images%2F$imageName?alt=media&token=e78b872b-fd3c-4dc1-aa12-f59efd22013f&_gl=1*14i0gum*_ga*MTIyNjczNzcxOC4xNjk2MzM2MjI2*_ga_CW55HF8NVT*MTY5NjQwMjk0MC4zLjEuMTY5NjQwMzUwNi4zMS4wLjA."

}