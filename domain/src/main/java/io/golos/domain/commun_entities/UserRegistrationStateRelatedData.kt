package io.golos.domain.mappers

import io.golos.commun4j.services.model.UserRegistrationStateResult
import io.golos.domain.requestmodel.RegistrationStepRequest

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-11.
 */
class UserRegistrationStateRelatedData(
    val requestResult: Any?,
    val request: RegistrationStepRequest,
    val stateRequestResult: UserRegistrationStateResult
)