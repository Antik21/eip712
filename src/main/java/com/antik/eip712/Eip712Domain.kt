package com.antik.eip712

import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.generated.Uint256

data class Eip712Domain(
    val name: Utf8String,
    val version: Utf8String,
    val chainId: Uint256,
    val verifyingContract: Address? = null
) : Structurable {

    constructor(name: String, version: String, chainId: Uint256) : this(
        Utf8String(name),
        Utf8String(version),
        chainId,
        null
    )

    constructor(name: String, version: String, chainId: Long) : this(
        Utf8String(name),
        Utf8String(version),
        Uint256(chainId),
        null
    )

    constructor(name: String, version: String, chainId: Long, address: String) : this(
        Utf8String(name),
        Utf8String(version),
        Uint256(chainId),
        Address(address)
    )

    override val typeName: String
        get() = "EIP712Domain"

    override fun eip712types(): List<Pair<String, Type<*>>> {
        val types = mutableListOf(
            Pair("name", name),
            Pair("version", version),
            Pair("chainId", chainId)
        )
        verifyingContract?.let {
            types.add(Pair("verifyingContract", it))
        }
        return types.toList()
    }
}

