package uz.fido.network.domain.model.widget

data class MainWidget(
    val id: Int,
    var name: String,
    var is_visible: Boolean,
    val order: Int
)