package uz.fido.network.domain.model.sign_up

enum class SignUpFlagsEnum(var flag: Int) {
    Continue(0),
    SignIn(1),
    Authenticate(2),
    Email(3),
    CardNumber(4);
}