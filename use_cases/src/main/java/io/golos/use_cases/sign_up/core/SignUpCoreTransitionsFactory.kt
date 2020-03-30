package io.golos.use_cases.sign_up.core

import io.golos.domain.*
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.domain.fingerprint.FingerprintAuthManager
import io.golos.domain.repositories.AuthRepository
import io.golos.use_cases.auth.AuthUseCase
import io.golos.use_cases.sign_up.core.data_structs.*
import io.golos.use_cases.sign_up.transitions.*
import java.lang.UnsupportedOperationException
import javax.inject.Inject
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class SignUpCoreTransitionsFactory
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val parent: SignUpSMCoreTransition,
    private val stringsConverter: StringsConverter,
    private val encryptor: Encryptor,
    private val keyValueStorage: KeyValueStorageFacade,
    private val fingerprintAuthManager: FingerprintAuthManager,
    private val authUseCase: AuthUseCase,
    private val authRepository: AuthRepository,
    private val userKeyStore: UserKeyStore,
    private val signUpTokensRepository: SignUpTokensRepository
) {
    private val SignUpState.index: Int get() = getStateIndex(this)
    private val KClass<out SingUpEvent>.index: Int get() = getEventIndex(this)
    private val SingUpEvent.index: Int get() = getEventIndex(this::class)

    private val statesTotal = 11
    private val eventsTotal = 14

    private val matrix = Array(statesTotal) {Array<() -> SingUpTransition<SingUpEvent>>(eventsTotal) {
        { throw UnsupportedOperationException("This transition is not supported") }
    }}

    init {
        matrix[SignUpState.ENTERING_PASSWORD.index][PasswordEntered::class.index] = {
            FromEnteringPasswordOnPasswordEntered(parent) as SingUpTransition<SingUpEvent>
        }

        matrix[SignUpState.ENTERING_PIN.index][PinCodeEntered::class.index] = {
            FromEnteringPinCodeOnPinCodeEntered(
                dispatchersProvider,
                parent,
                stringsConverter,
                encryptor,
                keyValueStorage,
                fingerprintAuthManager,
                authUseCase) as SingUpTransition<SingUpEvent>
        }

        matrix[SignUpState.ENTERING_USER_NAME.index][UserNameEntered::class.index] = {
            FromEnteringUserNameOnUserNameEntered(parent, authRepository) as SingUpTransition<SingUpEvent>
        }

        matrix[SignUpState.PASSWORD_CONFIRMATION.index][PasswordConfirmationEntered::class.index] = {
            FromPasswordConfirmationOnPasswordConfirmationEntered(
                dispatchersProvider,
                parent,
                authRepository,
                userKeyStore) as SingUpTransition<SingUpEvent>
        }

        matrix[SignUpState.PHONE_VERIFICATION.index][PhoneVerificationCodeEntered::class.index] = {
            FromPhoneVerificationOnCodeEntered(parent, authRepository) as SingUpTransition<SingUpEvent>
        }

        matrix[SignUpState.PHONE_VERIFICATION.index][PhoneVerificationCodeResend::class.index] = {
            FromPhoneVerificationOnResendCode(parent, authRepository) as SingUpTransition<SingUpEvent>
        }

        matrix[SignUpState.SELECTING_METHOD_TO_UNLOCK.index][UnlockMethodSelected::class.index] = {
            FromSelectingMethodToUnlockOnUnlockMethodSelected(
                dispatchersProvider,
                parent,
                keyValueStorage,
                authUseCase) as SingUpTransition<SingUpEvent>
        }

        matrix[SignUpState.SELECTING_SIGN_UP_METHOD.index][FbSelected::class.index] = {
            FromSelectingSignUpMethodOnFbSelected(parent) as SingUpTransition<SingUpEvent>
        }

        matrix[SignUpState.SELECTING_SIGN_UP_METHOD.index][GoogleSelected::class.index] = {
            FromSelectingSignUpMethodOnGoogleSelected(parent) as SingUpTransition<SingUpEvent>
        }

        matrix[SignUpState.SELECTING_SIGN_UP_METHOD.index][PhoneSelected::class.index] = {
            FromSelectingSignUpMethodOnPhoneSelected(parent) as SingUpTransition<SingUpEvent>
        }

        matrix[SignUpState.WAITING_FOR_FB_TOKEN.index][TokenReceived::class.index] = {
            FromWaitingForFbTokenOnTokenReceived(parent, signUpTokensRepository) as SingUpTransition<SingUpEvent>
        }

        matrix[SignUpState.WAITING_FOR_FB_TOKEN.index][TokenReceivingError::class.index] = {
            FromWaitingForFbTokenOnTokenReceivingError(parent) as SingUpTransition<SingUpEvent>
        }

        matrix[SignUpState.WAITING_FOR_GOOGLE_TOKEN.index][TokenReceived::class.index] = {
            FromWaitingForGoogleTokenOnTokenReceived(parent, signUpTokensRepository) as SingUpTransition<SingUpEvent>
        }

        matrix[SignUpState.WAITING_FOR_GOOGLE_TOKEN.index][TokenReceivingError::class.index] = {
            FromWaitingForGoogleTokenOnTokenReceivingError(parent) as SingUpTransition<SingUpEvent>
        }

        matrix[SignUpState.WAITING_FOR_PHONE.index][PhoneEntered::class.index] = {
            FromWaitingForPhoneOnPhoneEntered(parent, authRepository) as SingUpTransition<SingUpEvent>
        }
    }

    fun getTransition(from: SignUpState, on: SingUpEvent): SingUpTransition<SingUpEvent> = matrix[from.index][on.index]()

    private fun getStateIndex(state: SignUpState): Int = 
        when(state) {
            SignUpState.FINAL -> 0
            SignUpState.SELECTING_SIGN_UP_METHOD -> 1
            SignUpState.WAITING_FOR_GOOGLE_TOKEN -> 2
            SignUpState.WAITING_FOR_FB_TOKEN -> 3
            SignUpState.WAITING_FOR_PHONE -> 4
            SignUpState.PHONE_VERIFICATION -> 5
            SignUpState.ENTERING_USER_NAME -> 6
            SignUpState.ENTERING_PASSWORD -> 7
            SignUpState.PASSWORD_CONFIRMATION -> 8
            SignUpState.ENTERING_PIN -> 9
            SignUpState.SELECTING_METHOD_TO_UNLOCK -> 10
        }

    private fun getEventIndex(event: KClass<out SingUpEvent>): Int =
        when(event) {
            GoogleSelected::class -> 0
            FbSelected::class -> 1
            PhoneSelected::class -> 2
            TokenReceived::class -> 3
            TokenReceivingError::class -> 4
            IdentityRequestCompleted::class -> 5
            PhoneEntered::class -> 6
            PhoneVerificationCodeEntered::class -> 7
            PhoneVerificationCodeResend::class -> 8
            UserNameEntered::class -> 9
            PasswordEntered::class -> 10
            PasswordConfirmationEntered::class -> 11
            PinCodeEntered::class -> 12
            UnlockMethodSelected::class -> 13
            else -> throw UnsupportedOperationException("This class is not supported: ${event.simpleName}")
         }
}