package io.golos.use_cases.sign_up.transitions

import io.golos.domain.SignUpTokensRepository
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.use_cases.sign_up.core.SignUpSMCoreTransition

class FromWaitingForGoogleTokenOnTokenReceived(
    parent: SignUpSMCoreTransition,
    signUpTokensRepository: SignUpTokensRepository
) : OnTokenReceivedBase(parent, signUpTokensRepository, SignUpState.WAITING_FOR_GOOGLE_TOKEN) {

    override suspend fun getIdentity(accessToken: String) = signUpTokensRepository.getGoogleIdentity(accessToken)
}