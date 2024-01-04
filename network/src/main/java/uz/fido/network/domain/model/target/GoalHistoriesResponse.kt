package uz.fido.network.domain.model.target

class GoalHistoriesResponse(
    var transact_list: ArrayList<GoalHistory>,
    var request_id: String,
    var code: Int,
    var msg: String
)