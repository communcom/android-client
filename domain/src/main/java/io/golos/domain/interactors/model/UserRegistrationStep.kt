package io.golos.domain.interactors.model

import io.golos.cyber4j.model.CyberName
import io.golos.domain.Model

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-11.
 */
sealed class NextRegistrationStepRequestModel(open val phone: String) : Model

data class GetUserRegistrationStepRequestModel(override val phone: String) : NextRegistrationStepRequestModel(phone)

data class SendSmsForVerificationRequestModel(override val phone: String) : NextRegistrationStepRequestModel(phone)

data class SendVerificationCodeRequestModel(override val phone: String, val code: Int) :
    NextRegistrationStepRequestModel(phone)

data class SetUserNameRequestModel(override val phone: String, val userName: CyberName) :
    NextRegistrationStepRequestModel(phone)

data class WriteUserToBlockChainRequestModel(override val phone: String, val userName: CyberName) :
    NextRegistrationStepRequestModel(phone)

data class ResendSmsVerificationCodeModel(override val phone: String) : NextRegistrationStepRequestModel(phone)