package com.antik.eip712.signer

import com.antik.eip712.Eip712Domain
import com.antik.eip712.Eip712Encoder.typedDataToSignedBytes
import com.antik.eip712.Structurable
import com.antik.eip712.signer.EthSigner.Companion.getEthereumMessageHash
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECDSASignature
import org.web3j.crypto.Keys
import org.web3j.crypto.MnemonicUtils
import org.web3j.crypto.Sign
import org.web3j.utils.Numeric
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.math.BigInteger
import java.util.Arrays

class PrivateKeyEthSigner : EthSigner {

    private var credentials: Credentials
    override var domain: Eip712Domain

    constructor(name: String, version: String, credentials: Credentials, chainId: Long) {
        this.credentials = credentials
        this.domain = Eip712Domain(name, version, chainId, credentials.address)
    }

    constructor(name: String, version: String, privateKey: String, chainId: Long) {
        this.credentials = Credentials.create(privateKey)
        this.domain = Eip712Domain(name, version, chainId, credentials.address)
    }

    constructor(domain: Eip712Domain, privateKey: String?) {
        this.credentials = Credentials.create(privateKey)
        this.domain = domain
    }

    override val address: String
        get() = credentials.address

    override val privateKey: BigInteger
        get() = credentials.ecKeyPair.privateKey

    override fun <S : Structurable> signTypedData(domain: Eip712Domain, typedData: S): String {
        return this.signMessage(typedDataToSignedBytes(domain, typedData), false)
    }

    override fun <S : Structurable> verifyTypedData(
        domain: Eip712Domain,
        typedData: S,
        signature: String
    ): Boolean {
        return this.verifySignature(signature, typedDataToSignedBytes(domain, typedData), false)
    }

    override fun signMessage(message: ByteArray): String {
        return this.signMessage(message, true)
    }

    override fun signMessage(message: ByteArray, addPrefix: Boolean): String {
        val sig = if (addPrefix)
            Sign.signPrefixedMessage(message, credentials.ecKeyPair)
        else
            Sign.signMessage(message, credentials.ecKeyPair, false)

        val output = ByteArrayOutputStream()

        try {
            output.write(sig.r)
            output.write(sig.s)
            output.write(sig.v)
        } catch (e: IOException) {
            throw IllegalStateException("Error when creating ETH signature", e)
        }

        return Numeric.toHexString(output.toByteArray())
    }

    override fun verifySignature(signature: String, message: ByteArray): Boolean {
        return this.verifySignature(signature, message, true)
    }

    override fun verifySignature(signature: String, message: ByteArray, prefixed: Boolean): Boolean {
        val messageHash = if (prefixed) getEthereumMessageHash(message) else message

        val address = ecrecover(Numeric.hexStringToByteArray(signature), messageHash)

        return address.equals(this.address, ignoreCase = true)
    }

    companion object {
        fun fromMnemonic(name: String, version: String, mnemonic: String, chainId: Long): PrivateKeyEthSigner {
            val credentials = generateCredentialsFromMnemonic(mnemonic, 0)
            return PrivateKeyEthSigner(name, version, credentials, chainId)
        }

        fun fromMnemonic(
            name: String,
            version: String,
            mnemonic: String,
            accountIndex: Int,
            chainId: Long
        ): PrivateKeyEthSigner {
            val credentials = generateCredentialsFromMnemonic(mnemonic, accountIndex)
            return PrivateKeyEthSigner(name, version, credentials, chainId)
        }

        private fun ecrecover(signature: ByteArray, hash: ByteArray): String {
            val sig = ECDSASignature(
                Numeric.toBigInt(Arrays.copyOfRange(signature, 0, 32)),
                Numeric.toBigInt(Arrays.copyOfRange(signature, 32, 64))
            )

            val v = signature[64]
            val recId = if (v >= 3) {
                v - 27
            } else {
                v.toInt()
            }

            val recovered = Sign.recoverFromSignature(recId, sig, hash)
            return "0x" + Keys.getAddress(recovered)
        }

        private fun generateCredentialsFromMnemonic(mnemonic: String, accountIndex: Int): Credentials {
            // m/44'/60'/0'/0 derivation path
            val derivationPath = intArrayOf(
                44 or Bip32ECKeyPair.HARDENED_BIT, 60 or Bip32ECKeyPair.HARDENED_BIT,
                0 or Bip32ECKeyPair.HARDENED_BIT, 0, accountIndex
            )

            // Generate a BIP32 master keypair from the mnemonic phrase
            val masterKeypair = Bip32ECKeyPair.generateKeyPair(
                MnemonicUtils.generateSeed(mnemonic, "")
            )

            // Derive the keypair using the derivation path
            val derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, derivationPath)

            // Load the wallet for the derived keypair
            return Credentials.create(derivedKeyPair)
        }
    }
}