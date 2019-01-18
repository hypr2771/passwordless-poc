package org.passwordless

import java.math.BigInteger

data class UserDto(
    val publicKey: String,

    // FIXME: Debug purpose
    val privateKey: String
) {
    fun toUser(id: BigInteger): User {
        return User(
            id,
            publicKey,
            "",
            "",

            // FIXME: Debug purpose
            privateKey,
            ""
        )
    }
}