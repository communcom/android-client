package io.golos.domain.mappers

import io.golos.commun4j.services.model.FirstRegistrationStepResult
import io.golos.commun4j.services.model.UserRegistrationState
import io.golos.domain.commun_entities.UserRegistrationStateRelatedData
import io.golos.domain.dto.*
import io.golos.domain.requestmodel.SetUserKeysRequest
import java.util.*

object UserRegistrationStateEntityMapper {
    fun map(communObject: UserRegistrationStateRelatedData): UserRegistrationStateEntity {
        val stateRequestResult = communObject.stateRequestResult
        val requestResult = communObject.requestResult
        val stateRequest = communObject.request

        return when (communObject.stateRequestResult.currentState) {

            UserRegistrationState.REGISTERED -> RegisteredUser(
                stateRequestResult.data?.username,
                (stateRequest as? SetUserKeysRequest)?.masterKey
            )
            UserRegistrationState.TO_BLOCK_CHAIN -> {
                val userData = communObject.stateRequestResult.data
                    ?: throw IllegalStateException(
                        "server didn't returned user data for some reason"
                    )
                return UnWrittenToBlockChainUser(userData.username, userData.userId.name)
            }
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