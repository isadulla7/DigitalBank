package uz.fido.network.domain.model.abc_base

import java.io.Serializable
import java.math.BigInteger

data class SwapKeysRequest(
    val device_code: String,
    val public_key1: BigInteger,
    val public_key2: BigInteger,
    val encryptData: String,
) : Serializable