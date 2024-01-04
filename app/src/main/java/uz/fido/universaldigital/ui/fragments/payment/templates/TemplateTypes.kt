package uz.fido.universaldigital.ui.fragments.payment.templates

enum class TemplateTypes(type: String) {

    DEFAULT("D"),
    TRANSFER_VIA_MOBILE("M"),
    TRANSFER_VIA_CARD("C"),
    SWIFT_TRANSFER("S"),
    TRANSFER_VIA_WALLET("W"),
    REQUISITES("R");

    var templateType: String = "D"

    init {
        this.templateType = type
    }

    override fun toString(): String {
        return templateType.toString()
    }
}