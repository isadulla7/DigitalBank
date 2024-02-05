package uz.fido.network.domain.model.monitoring

class DateItem : ListItem() {
    var date: String? = null
    var amount:String=""
    override val type = TYPE_DATE
}