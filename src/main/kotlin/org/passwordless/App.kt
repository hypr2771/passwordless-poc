package org.passwordless

import io.javalin.Javalin
import org.passwordless.constant.USER_COLLECTION
import java.math.BigDecimal
import java.security.KeyPair

fun main(args: Array<String>) {

    val app = Javalin.create().start(7000)

    // Return the whole list of users
    app.get("/") { ctx -> ctx.result(USER_COLLECTION.toString()) }

    // Register a user using his public key
    app.post("/register") { ctx ->
        val user = ctx.body<UserDto>()
        register(user)
    }

    // Get a challenge for the given user
    app.get("/get-challenge/:id") { ctx -> ctx.result(challenge(USER_COLLECTION[ctx.pathParam("id").toInt()])) }

    // Post a proposal and get authenticated
    app.post("/authenticate/:id") { ctx ->
        val user = USER_COLLECTION[ctx.pathParam("id").toInt()]
        val proposal = ctx.body()

        (decryptText(proposal, user.publicKey) == user.challenge).toString()
    }
}

fun register(user: UserDto) {
    val serverSideUserSpecificKeyPair = genKeyPair()

    save(user, serverSideUserSpecificKeyPair)
}

fun challenge(user: User): String {
    val random = generateRandomNumberOfSize(20)

    val challengedUser = update(user, random)

    return encryptText(random, challengedUser.serverSidePrivateKey)
}

private fun generateRandomNumberOfSize(size: Int) =
    // Get real random using TRNG instead of pseudo RNG
    BigDecimal(Math.random()).multiply(BigDecimal.TEN.pow(size)).toBigInteger().toString()

private fun save(user: UserDto, userSpecificKeyPair: KeyPair) {
    val newUser = user.toUser(USER_COLLECTION.size.toBigInteger())
        .copy(
            serverSidePrivateKey = asString(userSpecificKeyPair.private),
            serverSidePublicKey = asString(userSpecificKeyPair.public)
        )

    val newList = USER_COLLECTION.toMutableList()
    newList.add(USER_COLLECTION.size, newUser)

    USER_COLLECTION = newList
}

private fun update(user: User, rand: String): User {
    val challengedUser = user.copy(challenge = rand)

    val newList = USER_COLLECTION.toMutableList()
    newList[challengedUser.userId.intValueExact()] = challengedUser

    USER_COLLECTION = newList
    return challengedUser
}