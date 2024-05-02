package uz.fido.network.domain.model.news

data class NewsListResponse(
    val code: Int,
    val msg: String,
    val news_list: ArrayList<News>,
    val ora_msg: String
)