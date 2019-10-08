package io.golos.domain.mappers

import io.golos.commun4j.services.model.FirstRegistrationStepResult
import io.golos.commun4j.services.model.UserRegistrationState
import io.golos.domain.entities.*
import io.golos.domain.requestmodel.SetUserKeysRequest
import java.util.*

object UserRegistrationStateEntityMapper {
    fun map(communObject: UserRegistrationStateRelatedData): UserRegistrationStateEntity {
        val stateRequestResult = communObject.stateRequestResult
        val requestResult = communObject.requestResult
        val stateRequest = communObject.request

        return when (communObject.stateRequestResult.state
            ?: throw IllegalArgumentException("server didn't returned reg state of user")) {

            UserRegistrationState.REGISTERED -> RegisteredUser(
                stateRequestResult.user ?: throw IllegalStateException(
                    "server" +
                            "didn't returned user name for some reason"
                ),
                (stateRequest as? SetUserKeysRequest)?.masterKey
            )
            UserRegistrationState.TO_BLOCK_CHAIN -> UnWrittenToBlockChainUser(
                stateRequestResult.user ?: throw IllegalStateException(
                    "server" +
                            "didn't returned user name for some reason"
                )
            )
            UserRegistrationState.VERIFY -> {
                val firstStepResult = requestResult as? FirstRegistrationStepResult
                return if (firstStepResult == null) UnverifiedUser(Date(Long.MIN_VALUE))
                else {
                    UnverifiedUser(requestResult.nextSmsRetry, requestResult.code)
                }
            }
            UserRegistrationState.SET_USER_NAME -> VerifiedUserWithoutUserName()
            UserRegistrationState.FIRST_STEP -> UnregisteredUser()
        }
    }
}