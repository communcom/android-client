package io.golos.data.repositories.sign_up_tokens

data class SignUpTokensConfig(
    val googleAccessTokenClientId: String,
    val googleAccessTokenClientSecret: String,
    val googleAccessTokenUrl: String,

    val accessTokenBaseUrl: String
)