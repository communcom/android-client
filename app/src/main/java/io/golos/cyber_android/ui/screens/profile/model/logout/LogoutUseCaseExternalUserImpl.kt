package io.golos.cyber_android.ui.screens.profile.model.logout

import java.lang.UnsupportedOperationException
import javax.inject.Inject

class LogoutUseCaseExternalUserImpl
@Inject
constructor() : LogoutUseCase {
    override suspend fun logout() {
        throw UnsupportedOperationException("This operation is not supported")
    }
}