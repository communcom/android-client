package io.golos.domain.model

import io.golos.cyber4j.model.CyberName

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-11.
 */
sealed class RegistrationStepRequest(open val phone: String) : Identifiable {
    private val _id = Id()

    override val id: Identifiable.Id
        get() = _id


    inner class Id : Identifiable.Id() {
        val _phone = phone
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

class GetUserRegistrationStepRequest(phone: String) : RegistrationStepRequest(phone)

class SendSmsForVerificationRequest(phone: String, val testCode: String?) :
    RegistrationStepRequest(phone){
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

class SendVerificationCodeRequest(phone: String, val code: Int) : RegistrationStepRequest(phone){
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

class SetUserNameRequest(phone: String, val userName: CyberName) : RegistrationStepRequest(phone){
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
    phone: String,
    val userName: CyberName,
    val ownerKey: String,
    val activeKey: String,
    val postingKey: String,
    val memoKey: String
) : RegistrationStepRequest(phone){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as SetUserKeysRequest

        if (userName != other.userName) return false
        if (ownerKey != other.ownerKey) return false
        if (activeKey != other.activeKey) return false
        if (postingKey != other.postingKey) return false
        if (memoKey != other.memoKey) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + userName.hashCode()
        result = 31 * result + ownerKey.hashCode()
        result = 31 * result + activeKey.hashCode()
        result = 31 * result + postingKey.hashCode()
        result = 31 * result + memoKey.hashCode()
        return result
    }
}

class ResendSmsVerificationCode(phone: String) : RegistrationStepRequest(phone)