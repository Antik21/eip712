# EIP712 Android Library

EIP712 Android Library is a Kotlin-based library designed to simplify the integration of the EIP712 standard into Android applications, enabling seamless signing and structured data handling for Ethereum transactions.

## Features

- **EIP712 Standard**: Implements the EIP712 standard as outlined in [Ethereum Improvement Proposals](https://eips.ethereum.org/EIPS/eip-712), allowing Android apps to securely and effectively sign structured data for Ethereum smart contract interactions.
- **Kotlin-Optimized**: Built from the ground up in Kotlin, this library fits naturally into Android projects, leveraging Kotlin’s concise and expressive syntax to create clean, maintainable code.
- **Web3j Integration**: Utilizes the powerful [web3j library](https://github.com/hyperledger/web3j) for Ethereum blockchain operations, ensuring compatibility and ease of interaction with Ethereum smart contracts.

## Getting Started

To start using EIP712 Android Library, follow the installation steps below:

1. Add the library to your project’s dependencies.
2. Configure the required Ethereum parameters in your Android application.
3. Utilize the provided Kotlin APIs to sign structured data and interact with Ethereum smart contracts with ease.

## Example Usage

```kotlin
// Example code snippet for signing a structured EIP712 message

val eip712Data = SomeEip712Struct(
    ...
)

val domain = Eip712Domain(
        DOMAIN_NAME,
        VERSION,
        chainId
    )

val signature = signer.signTypedData(domain, eip712Data.value)
