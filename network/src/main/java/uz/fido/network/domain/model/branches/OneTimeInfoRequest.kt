package uz.fido.network.domain.model.branches

import java.io.Serializable

class OneTimeInfoRequest(
    var account_code: String = "",
    var bank_code: String = ""
) : Serializable