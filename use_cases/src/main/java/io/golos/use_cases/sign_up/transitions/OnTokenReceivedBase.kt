package io.golos.use_cases.sign_up.transitions

import io.golos.domain.SignUpTokensRepository
import io.golos.domain.dto.SignUpIdentityDomain
import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.use_cases.sign_up.dto.TransitionResult
import io.golos.use_cases.sign_up.core.SignUpSMCoreTransition
import io.golos.use_cases.sign_up.core.data_structs.*
import timber.log.Timber

abstract class OnTokenReceivedBase(
    parent: SignUpSMCoreTransition,
    protected val signUpTokensRepository: SignUpTokensRepository,
    state: SignUpState
) : SingUpTransitionBase<TokenReceived>(parent, state) {
    /**
     * Process event and return new state of the SM
     */
    override suspend fun process(event: TokenReceived, snapshot: SignUpSnapshotDomain): TransitionResult =
        try {
            parent.sendCommand(ShowLoading())
            val identityResult = getIdentity(event.token)
            parent.sendCommand(HideLoading())

            Timber.tag("ACCESS_TOKEN").d("identity: $identityResult")

            if(identityResult.identity == null) {
                if(identityResult.oauthState=="registered") {
                    parent.sendCommand(ShowError(SignUpMessageCode.ACCOUNT_ALREADY_REGISTERED))
                } else {
                    parent.sendCommand(ShowError(SignUpMessageCode.GENERAL_ERROR))
                }
                getResult()
            } else {
                parent.sendCommand(NavigateToUserName())
                getResult(SignUpState.ENTERING_USER_NAME, snapshot.copy(identity = identityResult.identity))
            }
        } catch (ex: Exception) {
            parent.sendCommand(HideLoading())

            Timber.e(ex)
            parent.sendCommand(ShowError(SignUpMessageCode.GENERAL_ERROR))
            getResult()
        }

    protected abstract suspend fun getIdentity(accessToken: String): SignUpIdentityDomain
}