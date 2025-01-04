package com.antik.eip712

import org.junit.BeforeClass
import org.junit.Test
import org.junit.Assert.assertEquals
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.generated.Uint128
import org.web3j.utils.Numeric

class Eip712EncoderTest {

    companion object {
        private lateinit var domain: Eip712Domain
        private lateinit var message: Mail

        @JvmStatic
        @BeforeClass
        fun setUp() {
            message = Mail(
                from = Person("Cow", "0xCD2a3d9F938E13CD947Ec05AbC7FE734Df8DD826"),
                to = Person("Bob", "0xbBbBBBBbbBBBbbbBbbBbbbbBBbBbbbbBbBbbBBbB"),
                contents = "Hello, Bob!"
            )

            domain = Eip712Domain(
                name = "Ether Mail",
                version = "1",
                chainId = 1L,
                address = "0xCcCCccccCCCCcCCCCCCcCcCccCcCCCcCcccccccC"
            )
        }
    }

    @Test
    fun testEncodeType() {
        val result = Eip712Encoder.encodeType(message.intoEip712Struct())
        assertEquals(
            "Mail(Person from,Person to,string contents)Person(string name,address wallet)",
            result
        )
    }

    @Test
    fun testHashEncodedType() {
        val hash = Eip712Encoder.typeHash(message.intoEip712Struct())
        assertEquals(
            "0xa0cedeb2dc280ba39b857546d74f5549c3a1d7bdc2dd96bf881f76108e23dac2",
            Numeric.toHexString(hash)
        )
    }

    @Test
    fun testEncodeContentsValue() {
        val hash = Eip712Encoder.encodeValue(message.contents).value
        assertEquals(
            "0xb5aadf3154a261abdd9086fc627b61efca26ae5702701d05cd2305f7c52a2fc8",
            Numeric.toHexString(hash)
        )
    }

    @Test
    fun testEncodePersonData() {
        val fromHash = Eip712Encoder.encodeValue(message.from.intoEip712Struct()).value
        val toHash = Eip712Encoder.encodeValue(message.to.intoEip712Struct()).value

        assertEquals(
            "0xfc71e5fa27ff56c350aa531bc129ebdf613b772b6604664f5d8dbe21b85eb0c8",
            Numeric.toHexString(fromHash)
        )
        assertEquals(
            "0xcd54f074a4af31b4411ff6a60c9719dbd559c221c8ac3492d9d872b041d703d1",
            Numeric.toHexString(toHash)
        )
    }

    @Test
    fun testEncodeMailData() {
        val data = Eip712Encoder.encodeValue(message.intoEip712Struct()).value
        assertEquals(
            "0xc52c0ee5d84264471806290a3f2c4cecfc5490626bf912d01f240d7a274b371e",
            Numeric.toHexString(data)
        )
    }

    @Test
    fun testEncodeDomainType() {
        val result = Eip712Encoder.encodeType(domain.intoEip712Struct())
        assertEquals(
            "EIP712Domain(string name,string version,uint256 chainId,address verifyingContract)",
            result
        )
    }

    @Test
    fun testEncodeDomainMemberValues() {
        val nameHash = Eip712Encoder.encodeValue(domain.name).value
        assertEquals(
            "0xc70ef06638535b4881fafcac8287e210e3769ff1a8e91f1b95d6246e61e4d3c6",
            Numeric.toHexString(nameHash)
        )

        val versionHash = Eip712Encoder.encodeValue(domain.version).value
        assertEquals(
            "0xc89efdaa54c0f20c7adf612882df0950f5a951637e0307cdcb4c672f298b8bc6",
            Numeric.toHexString(versionHash)
        )

        val chainIdHash = Eip712Encoder.encodeValue(domain.chainId).value
        assertEquals(
            "0x0000000000000000000000000000000000000000000000000000000000000001",
            Numeric.toHexString(chainIdHash)
        )

        val verifyingContractHash = Eip712Encoder.encodeValue(domain.verifyingContract!!).value
        assertEquals(
            "0x000000000000000000000000cccccccccccccccccccccccccccccccccccccccc",
            Numeric.toHexString(verifyingContractHash)
        )
    }

    @Test
    fun testEncodeDomainData() {
        val data = Eip712Encoder.encodeValue(domain.intoEip712Struct()).value
        assertEquals(
            "0xf2cee375fa42b42143804025fc449deafd50cc031ca257e0b194a650a912090f",
            Numeric.toHexString(data)
        )
    }

    @Test
    fun testTypedDataToSignedBytes() {
        val data = Eip712Encoder.typedDataToSignedBytes(domain, message)
        assertEquals(
            "0xbe609aee343fb3c4b28e1df9e632fca64fcfaede20f02e86244efddf30957bd2",
            Numeric.toHexString(data)
        )
    }

    @Test
    fun testEncodeTypes() {
        val address = Address("0xe1fab3efd74a77c23b426c302d96372140ff7d0c")
        val addressResult = Eip712Encoder.encodeValue(address).value
        assertEquals(
            "0x000000000000000000000000e1fab3efd74a77c23b426c302d96372140ff7d0c",
            Numeric.toHexString(addressResult)
        )

        val number = Uint128(123)
        val numberResult = Eip712Encoder.encodeValue(number).value
        assertEquals(
            "0x000000000000000000000000000000000000000000000000000000000000007b",
            Numeric.toHexString(numberResult)
        )
    }
}
