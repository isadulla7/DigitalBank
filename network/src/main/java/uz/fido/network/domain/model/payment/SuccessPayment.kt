package uz.fido.network.domain.model.payment

import com.google.gson.annotations.Expose

data class SuccessPayment (
        @Expose var name: String = "",
        @Expose var code: String = "",
        @Expose var value: String = ""
)