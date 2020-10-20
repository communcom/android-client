package io.golos.domain.use_cases.model

import io.golos.domain.Model

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-11.
 */
sealed class NextRegistrationStepRequestModel(
    open val phone: String?,
    open val identity: String?
) : Model

data class GetUserRegistrationStepRequestModel(override val phone: String?, override val identity: String?) : NextRegistrationStepRequestModel(phone, identity)

data class SendSmsForVerificationRequestModel(val captcha: String?, override val phone: String?, override val identity: String?) : NextRegistrationStepRequestModel(phone, identity)

data class SendVerificationCodeRequestModel(override val phone: String?, override val identity: String?,  val code: Int) :
    NextRegistrationStepRequestModel(phone, identity)

data class SetUserNameRequestModel(override val phone: String?, override val identity: String?, val userName: String) :
    NextRegistrationStepRequestModel(phone, identity)

data class WriteUserToBlockChainRequestModel(override val phone: String?, override val identity: String?, val userName: String, val userId: String) :
    NextRegistrationStepRequestModel(phone, identity)

data class ResendSmsVerificationCodeModel(override val phone: String?, override val identity: String?) : NextRegistrationStepRequestModel(phone, identity)