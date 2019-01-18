# Passwordless

Passwordless is a prototype of a possible use of asymmetric cryptography in the web authentication context.

It requires user to stock password in some sort of database, whether local (encrypted USB stick with plain text DB) or online (such as LastPass).

The goal is to avoid having passwords transiting over the network, and leaving only public messages transit.

## Proof of concept

It is very basic implementation, no database nor JWT tokens are yet implemented but it has proven to be possible using the following steps :

#### Registration of the user

1. User generates a *keypair **client-side***
2. User pushes his username along with his *public key* ([request](#registration-request))
3. Server check preconditions (check if user already exists, public key is indeed a public key,...)
4. Server generate a *keypair **server-side*** specific to the user
5. Server stocks both *server-side private key* and *client-side public key* in the user entity
6. Server returns the *server-side public key* ([response](#registration-response))
7. User stocks *username*, *server-side public key* and *client-side key pair* on a secured USB device (using [WebUSB](https://wicg.github.io/webusb/)) or on a web extension (such as LastPass)

#### User authentication

1. User requests a *challenge* from the server for a specific username ([request](#challenge-request))
2. Server computes a *random* (using [true RNG](https://random.org) or pseudo RNG)
3. Server stocks the generated *random* number along with user entity
4. Server returns an encrypted version of the *random* number using the user's specific *private key* (aka. *challenge*)  ([response](#challenge-response))
5. User decrypts the *challenge* using the *server-side public key*
6. User sends back the *random* number encrypted using his *client-side private key* (aka. *proposal*) ([request](#authenticate-request))
7. Server decrypts the *proposal* and check whether it corresponds to the current *challenge*
8. Server authenticate the user (using whether JWT, session token,...) and removes the current *random* number from the user entity ([response](#authenticate-response))

#### User disconnection

1. User sends a logging out request or time out
2. Server removes the potentially generated *random* number and invalidate the session or JWT token

## USB stick keyring

Stocking password through USB can be achieved through various methods :

#### Locally encrypted database

When using the web app, JavaScript can access to USB devices.

Therefore, it is possible to stock the following login information object on it, encrypted with a master password.
```
{
    "url": "",
    "username": "",
    "clientSidePublicKey": "",
    "clientSidePrivateKey": "",
    "serverSidePrivateKey": "",
    "jwtToken": ""
}
```

## Online keyring

The idea is the same than USB stick, but we may have the flexibility of a web based keyring but the drawback of having a master password transiting over the network.

## Flows

### <a name="registration"></a> `/registration`

#### <a name="registration-request"></a> Request
```
{
    "username": "toto",
    "clientSidePublicKey": "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJJQ3ahZbtYrtbhzSFc2vDB8z8egbMFppN1V2QtAjTjpcmXoPpH+3c48GSFpHtummTxXacJ8KIuDx7H19DMaEYMCAwEAAQ=="
}

```

#### <a name="registration-response"></a> Response

##### `200 Ok`
```
{
    "serverSidePublicKey": "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKkOup/LJq0PhhNUYhlZ5dqVa7+OUzTFAbhbehwK+xFhICtv8kfuyHsbUBa69WnoPFGGkeM9j3Q3QUcQn9AriAECAwEAAQ=="
}

```

### <a name="challenge"></a> `/challenge`

#### <a name="challenge-request"></a> Request
```
{
    "username": "toto"
}

```

#### <a name="challenge-response"></a> Response

##### `200 Ok`
```
{
    "challenge": "TMfAneTMmUWer5AWomzUS+sDEN++1hW//uzaixwXG3WzBV+CGn8RQjPhjDqWi9O0pVfOygN/s9UFJTZUQxdEiw=="
}

```

### <a name="authenticate"></a> `/authenticate`

#### <a name="authenticate-request"></a> Request
```
{
    "username": "toto",
    "proposal": "b49Tw3ZOB+ztsUNJmltj0fUtBIufoWFBGAzXX3e/CTmhjTlxlrNeVogqVlDaLY1VuxfwJAdHYOTEtJs3MOps3A=="
}

```

#### <a name="authenticate-response"></a> Response

##### `200 Ok`
```
{
    "jwtToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6InRvdG8iLCJqdGkiOiIyYzVlZjk1NC01YTMxLTQ5MjgtYTY0ZS1hNjk4ZDZkZTcxYTgiLCJpYXQiOjE1NDc4MDU4NjEsImV4cCI6MTU0NzgwOTUyNn0.UPH5PeGrwnuW3UO41iaESQgQ2Bd9D4xPePuSTEhpYsY"
}

```
