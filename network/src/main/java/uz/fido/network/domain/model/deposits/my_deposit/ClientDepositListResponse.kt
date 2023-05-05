package uz.fido.network.domain.model.deposits.my_deposit

data class ClientDepositListResponse(
    val code: Int,
    val `data`: ArrayList<ClientDeposit>,
    val msg: String
)