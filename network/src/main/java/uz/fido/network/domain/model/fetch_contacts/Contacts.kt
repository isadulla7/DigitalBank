package uz.fido.network.domain.model.fetch_contacts

data class Contacts(val id: String, val name:String) {
    var numbers = ArrayList<String>()
    var emails = ArrayList<String>()
}