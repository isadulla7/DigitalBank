package uz.fido.network.domain.model.chat

data class UploadImageRequest(
    val reference_name: String? = null,
    val base64data: String? = null,
    val category: String = "chat_image"
)