package com.antik.eip712

import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.Utf8String


class Mail(
    var from: Person = Person("Cow", "0xCD2a3d9F938E13CD947Ec05AbC7FE734Df8DD826"),
    var to: Person = Person("Bob", "0xbBbBBBBbbBBBbbbBbbBbbbbBBbBbbbbBbBbbBBbB"),
    var contents: Utf8String = Utf8String("Hello, Bob!")
) : Structurable {

    constructor(from: Person, to: Person, contents: String) : this(from, to, Utf8String(contents))

    override val typeName: String
        get() = "Mail"

    override fun eip712types(): List<Pair<String, Type<*>>> {
        return listOf(
            "from" to from.intoEip712Struct(),
            "to" to to.intoEip712Struct(),
            "contents" to contents
        )
    }
}

class Person(
    var name: Utf8String = Utf8String(""),
    var wallet: Address = Address("")
) : Structurable {

    constructor(name: String, wallet: String) : this(Utf8String(name), Address(wallet))

    override val typeName: String
        get() = "Person"

    override fun eip712types(): List<Pair<String, Type<*>>> {
        return listOf(
            "name" to name,
            "wallet" to wallet
        )
    }
}
