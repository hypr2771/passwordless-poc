package org.passwordless

import org.passwordless.constant.PUBLIC_KEY
import org.passwordless.constant.USER_COLLECTION
import java.math.BigInteger
import javax.crypto.BadPaddingException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


class HelloTest {

    @BeforeTest
    fun before() {
        USER_COLLECTION = List(0) { index -> User(index.toBigInteger(), "", "", "", "", "") }
    }

    @Test
    fun generateKeyPair() {
        val keyPair = genKeyPair()

        println(asString(keyPair.public))
        println(asString(keyPair.private))

        val encrypted = encryptText("TEST", keyPair.private)
        val decrypted = decryptText(encrypted, keyPair.public)

        assertEquals("TEST", decrypted)
    }

    @Test
    fun scenarioEverythingIsFine() {
        // Create a user for the test
        val keyPair = genKeyPair()
        val keyPairServerSide = genKeyPair()
        val user = User(
            BigInteger.ONE,
            keyPair.public,
            keyPairServerSide.private,
            "",

            // FIXME: Debug purpose
            keyPair.private,
            keyPairServerSide.public
        )

        // Generate a challenge server side for the user
        val challenge = challenge(user)

        // Simulate user registration in database
        val challengedUser = USER_COLLECTION[0]

        // Client decrypt the challenge from the server
        val challengeClientSide = decryptText(challenge, PUBLIC_KEY)

        // Client encrypt its challenge answer
        val encryptedChallengeClientSide = encryptText(challengeClientSide, user.privateKey)

        // Client send encrypted challenge
        val decryptedChallengeServerSide = decryptText(encryptedChallengeClientSide, user.publicKey)

        // User is identical
        assertEquals(user.userId, challengedUser.userId)
        assertEquals(user.publicKey, challengedUser.publicKey)
        assertEquals(user.privateKey, challengedUser.privateKey)
        // But user is now challenged
        assertNotEquals(user.challenge, challengedUser.challenge)

        // User passed the challenge
        assertEquals(challengedUser.challenge, decryptedChallengeServerSide)

        // Generate a JWT token for authentication
    }

    @Test(expected = BadPaddingException::class)
    fun scenarioSomeoneStealsTheChallengeShouldNotAuthenticate() {
        // Create a user for the test
        val keyPair = genKeyPair()
        val keyPairServerSide = genKeyPair()
        val user = User(
            BigInteger.ONE,
            keyPair.public,
            keyPairServerSide.private,
            "",

            // FIXME: Debug purpose
            keyPair.private,
            keyPairServerSide.public
        )

        // Generate a challenge server side for the user
        val challenge = challenge(user)

        // Client decrypt the challenge from the server
        val challengeClientSide = decryptText(challenge, PUBLIC_KEY)

        // The bad guy can also get the challenge,
        val badGuyKeyPair = genKeyPair()
        val badGuyKeyPairServerSide = genKeyPair()
        val badGuy = User(
            BigInteger.ZERO,
            badGuyKeyPair.public,
            badGuyKeyPairServerSide.private,
            "",

            // FIXME: Debug purpose
            badGuyKeyPair.private,
            badGuyKeyPairServerSide.public
        )

        // Bad guy encrypt its challenge answer
        val encryptedChallengeBadGuy = encryptText(challengeClientSide, badGuy.privateKey)

        // Bad guy tries to log in as user sending encrypted challenge
        decryptText(encryptedChallengeBadGuy, user.publicKey)
    }

    @Test(expected = BadPaddingException::class)
    fun encryptWithKeyPairAndDecryptWithAnotherOne() {
        val keyPair = genKeyPair()
        val keyPair2 = genKeyPair()

        val encrypted = encryptText("TEST", keyPair.private)
        decryptText(encrypted, keyPair2.public)
    }

    @Test
    fun clientSideSimulationDecryptAndEncrypt() {
        val decrypted = decryptText(
            "Rl3W9J0yZDLua7e3Gtw70/XfbtRdllzciTxpuGIFEvMZoVzwziHi+/45qNS81RMFbfoeFeHMzQugrJICTr4z8g==",
            "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALL4hVZB6lC3JkrYCbFFX1afHzEfcsybZLbR/1WF+zmRUVSBBRSNvMniw6M4DTSSivZyfvcJOkPbf/p0B0+BeXECAwEAAQ=="
        )
        val encrypted = encryptText(
            decrypted,
            "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAsr2DEh/nAbUv0DXKhK91HeKscwTbBVtwARa/fhbP9iTThifem+qbzqyoZnJ/NG3MrPnD/IzR7CoTefg0NhKI+wIDAQABAkAGj9qOAcnTUDmPJUpoEKD2FepK1rTLv42fHJyvHltIKAYhHpLBaM4QfHcnenhOjrnvlFaQjwsKXBPIm+wPLJ0xAiEA8bDkd367tTK+fVDAD74isqFAcFqza0E96qnxvk/LkUcCIQC9UodMun17Ac5JTvsGBB82UNrvGDVftXGSE4O6uwHErQIhAMtIL0FadOLYxWN9ka0sDN8Vxa86eoIRo9RGEDAzyVJtAiEAgkgHmXKAJSTB+0qhjatKe0s6zev/1sP6yoYpUVedSOUCIAeyS9wrczoXNX4W8XhNOB9iGxO7wd3IgGIk3p8UBM5+"
        )

        println(encrypted)
    }

}
