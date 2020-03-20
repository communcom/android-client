package io.golos.domain.repositories

import io.golos.domain.dto.UserRegistrationStateEntity
import io.golos.domain.requestmodel.RegistrationStepRequest

interface RegistrationRepository : Repository<UserRegistrationStateEntity, RegistrationStepRequest>