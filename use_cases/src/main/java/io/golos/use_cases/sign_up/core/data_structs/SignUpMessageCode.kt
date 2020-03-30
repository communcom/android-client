package io.golos.use_cases.sign_up.core.data_structs

enum class SignUpMessageCode {
    /**
     * Can't receive token for a social network
     */
    TOKEN_RECEIVING_ERROR,
    ACCOUNT_ALREADY_REGISTERED,
    PHONE_ALREADY_REGISTERED,
    GENERAL_ERROR,
    USERNAME_ALREADY_TAKEN,
    PASSWORDS_ARE_NOT_SAME,
    WRITE_TO_BLOCK_CHAIN_ERROR,
    AUTH_ERROR,
    INVALID_PHONE_VERIFICATION_CODE,
    TOO_MANY_RETRIES,
    TRY_LATER,

    PHONE_VERIFICATION_CODE_RESENT
}