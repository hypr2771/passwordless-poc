package org.passwordless

import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

private val cipher = Cipher.getInstance("RSA")!!

fun genKeyPair(): KeyPair {
    val keyGen = KeyPairGenerator.getInstance("RSA")
    keyGen.initialize(512)

    return keyGen.genKeyPair()
}

fun encryptText(msg: String, key: String): String {
    return encryptText(msg, fromStringPrivate(key))
}

fun decryptText(msg: String, key: String): String {
    return decryptText(msg, fromStringPublic(key))
}

fun encryptText(msg: String, key: PrivateKey): String {
    cipher.init(Cipher.ENCRYPT_MODE, key)
    return Base64.getEncoder().encodeToString(cipher.doFinal(msg.toByteArray(charset("UTF-8"))))
}

fun decryptText(msg: String, key: PublicKey): String {
    cipher.init(Cipher.DECRYPT_MODE, key)
    return String(cipher.doFinal(Base64.getDecoder().decode(msg)))
}

fun asString(key: Key): String {
    return Base64.getEncoder().encodeToString(key.encoded)
}

private fun fromString(key: String): ByteArray {
    return Base64.getDecoder().decode(key)
}

private fun fromStringPrivate(privateKey: String): PrivateKey {
    return KeyFactory.getInstance("RSA")
        .generatePrivate(PKCS8EncodedKeySpec(fromString(privateKey)))
}

private fun fromStringPublic(publicKey: String): PublicKey {
    return KeyFactory.getInstance("RSA")
        .generatePublic(X509EncodedKeySpec(fromString(publicKey)))
}