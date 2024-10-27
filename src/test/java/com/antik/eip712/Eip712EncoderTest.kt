package com.antik.eip712

import com.antik.eip712.Eip712Encoder.encodeType
import com.antik.eip712.Eip712Encoder.encodeValue
import com.antik.eip712.signer.EthSigner
import com.antik.eip712.signer.PrivateKeyEthSigner
import junit.framework.TestCase.assertTrue
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.generated.Uint128
import org.web3j.utils.Numeric


class Eip712EncoderTest {

    private lateinit var domain: Eip712Domain
    private lateinit var signer: EthSigner
    private lateinit var message : Message


    @Before
    fun setUp() {
        domain = Eip712Domain(
            "Ether Mail",
            "1",
            1L,
            "0xCcCCccccCCCCcCCCCCCcCcCccCcCCCcCcccccccC"
        )
        signer = PrivateKeyEthSigner(domain, "0x4c0883a6910395b96cdd01d22d9f0d7c9cfaf01f4fc7686f2a88e132d4e2c7b0")
        message = Message(
            "Hello, Bob!",
            "0xbBbBBBBbbBBBbbbBbbBbbbbBBbBbbbbBbBbbBBbB",
            "0xAaAaaaaaAaAAAaaAAAAaAaAaAAAAAAaAaAAAAaAa"
        )

    }

    @Test
    fun testEncodeType() {
        val result = encodeType(message.intoEip712Struct())

        assertEquals("Message(string contents,string from,string to)", result)
    }

    @Test
    fun testEncodeContentsValue() {
        val hash = encodeValue(message.contents).value

        assertEquals("0xb5aadf3154a261abdd9086fc627b61efca26ae5702701d05cd2305f7c52a2fc8", Numeric.toHexString(hash))
    }


    @Test
    fun testEncodeDomainType() {
        val result = encodeType(domain.intoEip712Struct())

        Assert.assertEquals(
            "EIP712Domain(string name,string version,uint256 chainId,address verifyingContract)",
            result
        )
    }

    @Test
    fun testEncodeDomainMemberValues() {
        run {
            val data = encodeValue(domain.name).value
            Assert.assertEquals(
                "0xc70ef06638535b4881fafcac8287e210e3769ff1a8e91f1b95d6246e61e4d3c6",
                Numeric.toHexString(data)
            )
        }
        run {
            val data = encodeValue(domain.version).value
            Assert.assertEquals(
                "0xc89efdaa54c0f20c7adf612882df0950f5a951637e0307cdcb4c672f298b8bc6",
                Numeric.toHexString(data)
            )
        }
        run {
            val data = encodeValue(domain.chainId).value
            Assert.assertEquals(
                "0x0000000000000000000000000000000000000000000000000000000000000001",
                Numeric.toHexString(data)
            )
        }
        run {
            val data = encodeValue(domain.verifyingContract!!).value
            Assert.assertEquals(
                "0x000000000000000000000000cccccccccccccccccccccccccccccccccccccccc",
                Numeric.toHexString(data)
            )
        }
    }

    @Test
    fun testEncodeDomainData() {
        val data = encodeValue(domain.intoEip712Struct()).value

        Assert.assertEquals(
            "0xf2cee375fa42b42143804025fc449deafd50cc031ca257e0b194a650a912090f",
            Numeric.toHexString(data)
        )
    }

    @Test
    fun testEncodeTypes() {
        run {
            val address = Address("0xe1fab3efd74a77c23b426c302d96372140ff7d0c")
            val result = encodeValue(address).value
            Assert.assertEquals(
                "0x000000000000000000000000e1fab3efd74a77c23b426c302d96372140ff7d0c",
                Numeric.toHexString(result)
            )
        }

        run {
            val number = Uint128(123)
            val result = encodeValue(number).value
            Assert.assertEquals(
                "0x000000000000000000000000000000000000000000000000000000000000007b",
                Numeric.toHexString(result)
            )
        }
    }

    @Test
    fun testVerifyTypedData() {
        run {
            // Подпишем данные
            val signature = signer.signTypedData(domain, message)

            // Проверка подписи
            val result = signer.verifyTypedData(domain, message, signature)

            // Подтвердим корректность подписи
            assertTrue(result)

            // Проверка с неверной подписью
            val fakeSignature = signature.dropLast(2) + "00"
            val fakeResult = signer.verifyTypedData(domain, message, fakeSignature)

            // Убедимся, что неправильная подпись не проходит проверку
            assertTrue(!fakeResult)
        }
    }
}
