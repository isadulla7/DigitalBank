package uz.fido.universaldigital.widgets.currency_rates.retrofit

import uz.fido.network.domain.model.rates.CourseItem

sealed class RepoResult
data class Success(val data: ArrayList<CourseItem>) : RepoResult()
data class Failure(val error: String) : RepoResult()
