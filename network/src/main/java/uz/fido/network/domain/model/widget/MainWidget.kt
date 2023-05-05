package uz.fido.network.domain.model.widget

data class MainWidget(
    val id: Int,
    val name: String,
    var is_visible: Boolean,
    val order: Int
)