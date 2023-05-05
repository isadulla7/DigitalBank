package uz.fido.network.domain.model.branches

data class BranchListResponse(
    val code: Int,
    val filials: ArrayList<Branches>,
    val msg: String
)