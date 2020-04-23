package io.golos.use_cases.sign_up.transitions

import io.golos.domain.DispatchersProvider
import io.golos.domain.UserKeyStore
import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.domain.repositories.AuthRepository
import io.golos.use_cases.sign_up.dto.TransitionResult
import io.golos.use_cases.sign_up.core.SignUpSMCoreTransition
import io.golos.use_cases.sign_up.core.data_structs.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber

class FromPasswordConfirmationOnPasswordConfirmationEntered(
    private val dispatchersProvider: DispatchersProvider,
    parent: SignUpSMCoreTransition,
    private val authRepository: AuthRepository,
    private val userKeyStore: UserKeyStore
) : SingUpTransitionBase<PasswordConfirmationEntered>(parent, SignUpState.PASSWORD_CONFIRMATION) {
    /**
     * Process event and return new state of the SM
     */
    override suspend fun process(event: PasswordConfirmationEntered, snapshot: SignUpSnapshotDomain): TransitionResult {
        val password = snapshot.password
        val confirmedPassword = event.password

        if(password != confirmedPassword) {
            parent.sendCommand(ShowError(SignUpMessageCode.PASSWORDS_ARE_NOT_SAME))
            return getResult()
        }

        return try {
            val userName = snapshot.userName!!
            val userId = snapshot.userId!!

            parent.sendCommand(ShowLoading())

            val keys = withContext(dispatchersProvider.ioDispatcher) {
                userKeyStore.createKeys(userId, userName, password)
            }

            val success = authRepository.writeUserToBlockChain(
                phone = snapshot.phoneNumber,
                identity = snapshot.identity,
                email = snapshot.email,
                userId = userId.userId,
                userName = userName,
                owner = keys.ownerPublicKey,
                active = keys.activePublicKey)
            delay(10000)

            parent.sendCommand(HideLoading())

            if(success) {
                parent.sendCommand(NavigateToPinCode())
                getResult(SignUpState.ENTERING_PIN)
            } else {
                parent.sendCommand(ShowError(SignUpMessageCode.WRITE_TO_BLOCK_CHAIN_ERROR))
                getResult(SignUpState.SELECTING_SIGN_UP_METHOD, clearSnapshot = true)
            }
        } catch (ex: Exception) {
            parent.sendCommand(HideLoading())
            Timber.e(ex)

            parent.sendCommand(ShowError(SignUpMessageCode.GENERAL_ERROR))
            getResult()
        }
    }
}