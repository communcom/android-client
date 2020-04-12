package io.golos.domain.requestmodel

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-11.
 */
sealed class RegistrationStepRequest(
    open val phone: String?,
    open val identity: String?) : Identifiable {
    private val _id = Id()

    override val id: Identifiable.Id
        get() = _id


    inner class Id : Identifiable.Id() {
        val _phone = phone ?: identity
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            if (_phone != other._phone) return false

            return true
        }

        override fun hashCode(): Int {
            return _phone.hashCode()
        }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RegistrationStepRequest

        if (phone != other.phone) return false

        return true
    }

    override fun hashCode(): Int {
        return phone.hashCode()
    }
}

class GetUserRegistrationStepRequest(phone: String?, identity: String?) : RegistrationStepRequest(phone, identity)

class SendSmsForVerificationRequest(val captcha: String?, phone: String?, identity: String?, val testCode: String) :
    RegistrationStepRequest(phone, identity){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as SendSmsForVerificationRequest

        if (testCode != other.testCode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (testCode?.hashCode() ?: 0)
        return result
    }
}

class SendVerificationCodeRequest(phone: String?, identity: String?, val code: Int) : RegistrationStepRequest(phone, identity){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as SendVerificationCodeRequest

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + code
        return result
    }
}

class SetUserNameRequest(phone: String?, identity: String?, val userName: String) : RegistrationStepRequest(phone, identity){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as SetUserNameRequest

        if (userName != other.userName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + userName.hashCode()
        return result
    }
}

class SetUserKeysRequest(
    phone: String?,
    identity: String?,
    val userId: String,
    val userName: String,
    val masterKey: String,
    val ownerPublicKey: String,
    val ownerPrivateKey: String,
    val activePublicKey: String,
    val activePrivateKey: String,
    val postingPublicKey: String,
    val postingPrivateKey: String,
    val memoPublicKey: String,
    val memoPrivateKey: String
) : RegistrationStepRequest(phone, identity){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as SetUserKeysRequest

        if (userId != other.userId) return false
        if (userName != other.userName) return false
        if (masterKey != other.masterKey) return false
        if (ownerPublicKey != other.ownerPublicKey) return false
        if (ownerPrivateKey != other.ownerPrivateKey) return false
        if (activePublicKey != other.activePublicKey) return false
        if (activePrivateKey != other.activePrivateKey) return false
        if (postingPublicKey != other.postingPublicKey) return false
        if (postingPrivateKey != other.postingPrivateKey) return false
        if (memoPublicKey != other.memoPublicKey) return false
        if (memoPrivateKey != other.memoPrivateKey) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + userName.hashCode()
        result = 31 * result + masterKey.hashCode()
        result = 31 * result + ownerPublicKey.hashCode()
        result = 31 * result + ownerPrivateKey.hashCode()
        result = 31 * result + activePublicKey.hashCode()
        result = 31 * result + activePrivateKey.hashCode()
        result = 31 * result + postingPublicKey.hashCode()
        result = 31 * result + postingPrivateKey.hashCode()
        result = 31 * result + memoPublicKey.hashCode()
        result = 31 * result + memoPrivateKey.hashCode()
        return result
    }
}

class ResendSmsVerificationCode(phone: String?, identity: String?) : RegistrationStepRequest(phone, identity)