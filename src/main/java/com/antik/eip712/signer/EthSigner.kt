package com.antik.eip712.signer

import com.antik.eip712.Eip712Domain
import com.antik.eip712.Structurable
import org.web3j.crypto.Hash
import org.web3j.utils.Numeric
import java.math.BigInteger

interface EthSigner {
    /**
     * Get wallet address
     *
     * @return Address in hex string
     */
    val address: String

    /**
     * Get Private key
     *
     * @return Private key
     */
    val privateKey: BigInteger

    /**
     * Get EIP712 domain
     *
     * @return Eip712 domain
     */
    val domain: Eip712Domain

    /**
     * Signs typed struct using ethereum private key by EIP-712 signature standard.
     *
     * @param <S> - EIP712 structure
     * @param domain - EIP712 domain
     * @param typedData - Object implementing EIP712 structure standard
     * @return Signature object
    </S> */
    fun <S : Structurable> signTypedData(domain: Eip712Domain, typedData: S): String

    /**
     * Verify typed EIP-712 struct standard.
     *
     * @param <S> - EIP712 structure
     * @param domain - EIP712 domain
     * @param typedData - Object implementing EIP712 structure standard
     * @param signature - Signature of the EIP-712 structures
     * @return true on verification success
    </S> */
    fun <S : Structurable> verifyTypedData(
        domain: Eip712Domain,
        typedData: S,
        signature: String
    ): Boolean

    /**
     * Sign raw message
     *
     * @param message - Message to sign
     * @return Signature object
     */
    fun signMessage(message: ByteArray): String

    /**
     * Sign raw message
     *
     * @param message - Message to sign
     * @param addPrefix - If true then add secure prefix ([EIP-712](https://eips.ethereum.org/EIPS/eip-712))
     * @return Signature object
     */
    fun signMessage(message: ByteArray, addPrefix: Boolean): String

    /**
     * Verify signature with raw message
     *
     * @param signature - Signature object
     * @param message - Message to verify
     * @return true on verification success
     */
    fun verifySignature(signature: String, message: ByteArray): Boolean

    /**
     * Verify signature with raw message
     *
     * @param signature - Signature object
     * @param message - Message to verify
     * @param prefixed - If true then add secure prefix ([EIP-712](https://eips.ethereum.org/EIPS/eip-712))
     * @return true on verification success
     */
    fun verifySignature(signature: String, message: ByteArray, prefixed: Boolean): Boolean

    companion object {
        fun getEthereumMessagePrefix(messageLength: Int): ByteArray {
            return (MESSAGE_PREFIX + messageLength.toString()).toByteArray()
        }

        @JvmStatic
        fun getEthereumMessageHash(message: ByteArray): ByteArray {
            val prefix = getEthereumMessagePrefix(message.size)

            val result = ByteArray(prefix.size + message.size)
            System.arraycopy(prefix, 0, result, 0, prefix.size)
            System.arraycopy(message, 0, result, prefix.size, message.size)

            return Hash.sha3(result)
        }

        const val MESSAGE_PREFIX: String = "\u0019Ethereum Signed Message:\n"
        const val MESSAGE_EIP712_PREFIX: String = "\u0019\u0001"
        val EIP1271_SUCCESS_VALUE: ByteArray = Numeric.hexStringToByteArray("0x1626ba7e")
    }
}