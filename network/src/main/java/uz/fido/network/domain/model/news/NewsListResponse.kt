package uz.fido.network.domain.model.news

import uz.fido.network.domain.model.news.News

data class NewsListResponse(
    val code: Int,
    val msg: String,
    val news_list: ArrayList<News>,
    val ora_msg: String
)