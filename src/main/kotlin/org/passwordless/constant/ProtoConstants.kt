package org.passwordless.constant

import org.passwordless.User
import org.passwordless.asString
import org.passwordless.genKeyPair

val pair = genKeyPair()
val PRIVATE_KEY =
    "MIIBUwIBADANBgkqhkiG9w0BAQEFAASCAT0wggE5AgEAAkEAha65nUF63vYZiGHT2NyZzI6nf966Uwt8CVX6cJwneVakteQRQfIgdnmpJexvMIwO+5c17WjQDlx27PNQxzO3JwIDAQABAkAJTjpZef9lmdf5KGYoyGJkXOHIAucRdNEzjSqbwCL3TTjuoMB7o1Aw/hCxWaz6/6wb9bPoKP2LiFfthJQqKdwBAiEAwRZwdc2OQg3/EamijTY7IHX65kFY0b1b4bge7mDWxQECIQCxPURLot2Ji0GYh0RAqc8+AKB4pPEIuX78WtkA8Pe0JwIgAlqirlz+MgH3rSBzUeNqXx/xGiOL0KrNVvgmsGNP2wECICKW6Fsyf7W2HakczO8ptK5PBQJGflvLh8LMIAmU/WKFAiA0hDsVar8cJYXRzqoQp7l67V9OatAVqoGoBG2s79fpMg=="
val PUBLIC_KEY =
    "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIWuuZ1Bet72GYhh09jcmcyOp3/eulMLfAlV+nCcJ3lWpLXkEUHyIHZ5qSXsbzCMDvuXNe1o0A5cduzzUMcztycCAwEAAQ=="
var USER_COLLECTION = List(0) { i ->
    val pair = genKeyPair()
    User(i.toBigInteger(), asString(pair.public), asString(pair.private), "", "", "")
}