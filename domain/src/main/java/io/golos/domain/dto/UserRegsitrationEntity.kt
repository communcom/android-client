package io.golos.domain.dto

import io.golos.domain.Entity
import java.util.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-11.
 */
sealed class UserRegistrationStateEntity : Entity

data class UnregisteredUser(private val type: String = "u_ss") : UserRegistrationStateEntity()

data class UnverifiedUser(val nextSmsVerification: Date, val smsCode: Int? = null) : UserRegistrationStateEntity()

data class VerifiedUserWithoutUserName(private val type: String = "v_s") : UserRegistrationStateEntity()

data class UnWrittenToBlockChainUser(val userName: String, val userId: String) : UserRegistrationStateEntity()

data class RegisteredUser(
    val userName: String,
    val masterPassword: String?
) : UserRegistrationStateEntity()