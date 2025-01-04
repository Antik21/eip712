package com.antik.eip712

import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.generated.Uint256

private const val NAME = "zkSync"
private const val VERSION = "2"

fun defaultDomain(chainId: Long): Eip712Domain {
    return Eip712Domain(Utf8String(NAME), Utf8String(VERSION), Uint256(chainId), null)
}
