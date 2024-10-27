package com.antik.eip712


import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.Utf8String

data class Message(
    val contents: Utf8String,
    val from: Utf8String,
    val to: Utf8String
) : Structurable {

    constructor(contents: String, from: String, to: String) : this(
        Utf8String(contents),
        Utf8String(from),
        Utf8String(to)
    )

    override val typeName: String
        get() = "Message"

    override fun eip712types(): List<Pair<String, Type<*>>> {
        return listOf(
            Pair("contents", contents),
            Pair("from", from),
            Pair("to", to)
        )
    }
}
