package com.antik.eip712

import com.antik.eip712.signer.EthSigner
import org.web3j.abi.datatypes.*
import org.web3j.abi.datatypes.generated.Bytes32
import org.web3j.crypto.Hash
import org.web3j.utils.Numeric
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.math.BigInteger

object Eip712Encoder {

    fun encodeValue(value: Type<*>): Bytes32 {
        return when (value) {
            is Utf8String -> Bytes32(Hash.sha3(value.value.toByteArray()))
            is Bytes32 -> value
            is NumericType -> Bytes32(Numeric.toBytesPadded(value.value as BigInteger, 32))
            is DynamicArray<*> -> {
                val members = value.value as List<Type<*>>
                val bytes = ByteArrayOutputStream()
                members.map(::encodeValue).forEach { bytes.write(it.value) }
                Bytes32(Hash.sha3(bytes.toByteArray()))
            }

            is BytesType -> Bytes32(Hash.sha3(value.value))
            is Address -> {
                val bytes = Numeric.hexStringToByteArray(value.value)
                val result = ByteArray(32)
                System.arraycopy(bytes, 0, result, 12, 20)
                Bytes32(result)
            }

            is Eip712Struct -> {
                val typeHash = typeHash(value)
                val members = value.value.eip712types()
                val bytes = ByteArrayOutputStream()
                bytes.write(typeHash)
                members.forEach { member ->
                    bytes.write(encodeValue(member.second).value)
                }
                Bytes32(Hash.sha3(bytes.toByteArray()))
            }

            else -> throw IllegalArgumentException("Unsupported ethereum type: \"${value.typeAsString}\"")
        }
    }

    fun encodeType(structure: Eip712Struct): String {
        val sb = StringBuilder(structure.encodeType())
        dependencies(structure).forEach { dep -> sb.append(dep.encodeType()) }
        return sb.toString()
    }

    fun typeHash(structure: Eip712Struct): ByteArray {
        return Hash.sha3(encodeType(structure).toByteArray())
    }

    fun dependencies(structure: Eip712Struct): Set<Eip712Struct> {
        val result = sortedSetOf<Eip712Struct>()
        structure.value.eip712types().forEach { value ->
            if (value.second is Eip712Struct) {
                result.add(value.second as Eip712Struct)
            }
        }
        return result
    }

    fun <S : Structurable> typedDataToSignedBytes(domain: Eip712Domain, typedData: S): ByteArray {
        val output = ByteArrayOutputStream()
        try {
            output.write(EthSigner.MESSAGE_EIP712_PREFIX.toByteArray())
            output.write(encodeValue(domain.intoEip712Struct()).value)
            output.write(encodeValue(typedData.intoEip712Struct()).value)
        } catch (e: IOException) {
            throw IllegalStateException("Error when creating ETH signature", e)
        }
        return Hash.sha3(output.toByteArray())
    }
}
