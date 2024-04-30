package uz.fido.utils.utility.activity

fun String.insertStringBetween(insert: String, index: Int): String {
    return StringBuilder(this).insert(index, insert).toString()
}
