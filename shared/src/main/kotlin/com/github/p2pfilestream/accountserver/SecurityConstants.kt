package com.github.p2pfilestream.accountserver

val NICKNAME_JWT_SECRET = System.getenv("NICKNAME_JWT_SECRET")
        ?: "SecretKeyToGenJWTs"
