package uz.fido.network.domain.model.target

import uz.fido.network.domain.model.fetch_contacts.Contact

interface DialogDismissListener {
    fun dismissed() {}
    fun dismiss(image: GoalImage) {}
    fun dismissContactDialog(arrayList: ArrayList<Contact>){}
}