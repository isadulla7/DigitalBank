package uz.fido.network.domain.model.target

class GoalImage(
    var bgColor: Int,
    var image: Int,
    var image_name: String,
    var goal_name: String? = "Например, имя \n" + "цели"
)