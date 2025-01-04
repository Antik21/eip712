package com.antik.eip712

import com.antik.eip712.signer.PrivateKeyEthSigner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import org.web3j.crypto.Credentials
import org.web3j.crypto.Hash

class PrivateKeyEthSignerTest {

    companion object {
        private lateinit var credentials: Credentials
        private lateinit var key: PrivateKeyEthSigner
        private lateinit var domain: Eip712Domain
        private lateinit var message: Mail

        @BeforeClass
        @JvmStatic
        fun setUp() {
            val privateKey = Hash.sha3String("cow")

            credentials = Credentials.create(privateKey)
            key = PrivateKeyEthSigner(credentials)

            domain = Eip712Domain(
                "Ether Mail",
                "1",
                1L,
                "0xCcCCccccCCCCcCCCCCCcCcCccCcCCCcCcccccccC"
            )

            message = Mail()
        }
    }

    @Test
    fun testSignTypedData() {
        val signature = key.signTypedData(domain, message)

        assertEquals(
            "0x4355c47d63924e8a72e509b65029052eb6c299d53a04e167c5775fd466751c9d07299936d304c153f6443dfa05f40ff007d72911b6f72307f996231605b915621c",
            signature
        )
    }

    @Test
    fun testVerifySignedTypedData() {
        val signature =
            "0x4355c47d63924e8a72e509b65029052eb6c299d53a04e167c5775fd466751c9d07299936d304c153f6443dfa05f40ff007d72911b6f72307f996231605b915621c"

        val verified = key.verifyTypedData(domain, message, signature)

        assertTrue(verified)
    }
}
