package com.antik.eip712

import org.web3j.abi.datatypes.Type

interface Structurable {

    val typeName: String

    fun eip712types(): List<Pair<String, Type<*>>>

    fun intoEip712Struct(): Eip712Struct {
        return Eip712Struct(this)
    }
}