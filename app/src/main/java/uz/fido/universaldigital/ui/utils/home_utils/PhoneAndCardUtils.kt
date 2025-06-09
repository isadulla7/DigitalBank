package uz.fido.universaldigital.ui.utils.home_utils

fun applyMask(mask: String, text: String): String {
    var maskedText = ""
    var index = 0

    for (char in mask) {
        if (char == '#') {
            if (index < text.length) {
                maskedText += text[index]
                index++
            } else {
                break
            }
        } else {
            maskedText += char
        }
    }

    return maskedText
}

fun mobileServiceId(text:String):String{
   return when (text.substring(3, 5)) {
        "90", "91" -> {
            "51"
        }

        "93", "94","50" -> {
            "53"
        }

        "99", "95" -> "687"
        "97", "88" -> "54"
        "98" -> "52"
        else -> "error"
    }
}