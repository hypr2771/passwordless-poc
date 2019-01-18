package org.passwordless

import com.fasterxml.jackson.databind.ObjectMapper
import java.math.BigInteger
import java.security.PrivateKey
import java.security.PublicKey

data class User(
    var userId: BigInteger,
    val publicKey: String,
    val serverSidePrivateKey: String,
    var challenge: String,

    // FIXME: Debug purpose
    val privateKey: String,
    val serverSidePublicKey: String
) {
    constructor(
        userId: BigInteger,
        publicKey: PublicKey,
        serverSidePrivateKey: PrivateKey,
        challenge: String,

        // FIXME: Debug purpose
        privateKey: PrivateKey,
        serverSidePublicKey: PublicKey
    ) : this(
        userId,
        asString(publicKey),
        asString(serverSidePrivateKey),
        challenge,

        // FIXME: Debug purpose
        asString(privateKey),
        asString(serverSidePublicKey)
    )

    override fun toString(): String {
        return ObjectMapper().writeValueAsString(this)
    }
}