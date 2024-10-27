package com.antik.eip712

import org.web3j.abi.datatypes.Type


open class Eip712Struct(
    private val structure: Structurable
) : Type<Structurable>, Comparable<Eip712Struct> {

    override fun getValue(): Structurable {
        return structure
    }

    override fun getTypeAsString(): String {
        return structure.typeName
    }

    override fun compareTo(other: Eip712Struct): Int {
        return this.typeAsString.compareTo(other.typeAsString)
    }

    fun encodeType(): String {
        return structure.typeName + "(" +
                structure.eip712types()
                    .joinToString(",") { entry ->
                        val typeString = entry.second.typeAsString
                        "$typeString ${entry.first}"
                    } +
                ")"
    }
}
