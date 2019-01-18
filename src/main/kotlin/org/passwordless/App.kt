package org.passwordless

import io.javalin.Javalin
import org.passwordless.constant.USER_COLLECTION
import java.math.BigDecimal

fun main(args: Array<String>) {

    val app = Javalin.create().start(7000)

    app.get("/") { ctx -> ctx.result(USER_COLLECTION.toString()) }

    app.post("/register") { ctx ->
        val user = ctx.body<UserDto>()

        val userSpecificKeyPair = genKeyPair()

        val newUser = user.toUser(USER_COLLECTION.size.toBigInteger())
            .copy(serverSidePrivateKey = asString(userSpecificKeyPair.private),
                serverSidePublicKey = asString(userSpecificKeyPair.public))

        val newList = USER_COLLECTION.toMutableList()
        newList.add(USER_COLLECTION.size, newUser)

        USER_COLLECTION = newList
    }

    app.get("/get-challenge/:id") { ctx -> ctx.result(challenge(USER_COLLECTION[ctx.pathParam("id").toInt()])) }

    app.post("/authenticate/:id") { ctx ->
        val user = USER_COLLECTION[ctx.pathParam("id").toInt()]
        val proposal = ctx.body()

        (decryptText(proposal, user.publicKey) == user.challenge).toString()
    }
}

fun challenge(user: User): String {
    // Get real random using TRNG instead of pseudo RNG
    val rand = BigDecimal(Math.random()).multiply(BigDecimal.TEN.pow(20)).toBigInteger().toString()

    val challengedUser = user.copy(challenge = rand)

    val newList = USER_COLLECTION.toMutableList()
    newList[challengedUser.userId.intValueExact()] = challengedUser

    USER_COLLECTION = newList

    return encryptText(rand, challengedUser.serverSidePrivateKey)
}