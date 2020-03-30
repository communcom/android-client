@file:Suppress("CanSealedSubClassBeObject")

package io.golos.use_cases.sign_up.core.data_structs

import io.golos.domain.dto.AppUnlockWay
import io.golos.domain.dto.SignUpIdentityDomain

sealed class SingUpEvent

class GoogleSelected: SingUpEvent()
class FbSelected: SingUpEvent()
class PhoneSelected: SingUpEvent()

data class TokenReceived(val token: String): SingUpEvent()
class TokenReceivingError: SingUpEvent()

data class IdentityRequestCompleted(val identity: SignUpIdentityDomain): SingUpEvent()

data class PhoneEntered(val phone: String, val captcha: String): SingUpEvent()
data class PhoneVerificationCodeEntered(val verificationCode: Int): SingUpEvent()
class PhoneVerificationCodeResend: SingUpEvent()

data class UserNameEntered(val userName: String): SingUpEvent()

data class PasswordEntered(val password: String): SingUpEvent()
data class PasswordConfirmationEntered(val password: String): SingUpEvent()

data class PinCodeEntered(val pinCode: String): SingUpEvent()

data class UnlockMethodSelected(val unlockMethod: AppUnlockWay): SingUpEvent()


