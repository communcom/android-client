package io.golos.domain.use_cases.model

import io.golos.domain.Model
import java.util.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-11.
 */
sealed class UserRegistrationStateModel : Model

class UnregisteredUserModel : UserRegistrationStateModel() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

data class UnverifiedUserModel(val nextSmsVerification: Date, val smsCode: Int? = null) : UserRegistrationStateModel()

class VerifiedUserWithoutUserNameModel : UserRegistrationStateModel() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

data class UnWrittenToBlockChainUserModel(val userName: String, val userId: String) : UserRegistrationStateModel()

data class RegisteredUserModel(val userName: String) : UserRegistrationStateModel()