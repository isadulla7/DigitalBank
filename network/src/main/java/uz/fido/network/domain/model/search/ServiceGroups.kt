package uz.fido.network.domain.model.search

import java.io.Serializable

class ServiceGroups(
    val id: String,
    val oparations: ArrayList<SearchOperation>,
    val name: String,
    val image_url: String
) : Serializable
