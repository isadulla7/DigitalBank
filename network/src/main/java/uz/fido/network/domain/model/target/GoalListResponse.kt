package uz.fido.network.domain.model.target

class GoalListResponse(
    var request_id: String,
    var code: Int,
    var user_target_list: ArrayList<GoalModel>
)