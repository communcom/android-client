package io.golos.cyber_android.ui.screens.profile.model.logout

import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.UserKeyStore
import io.golos.domain.repositories.UsersRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LogoutUseCaseImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val userKeyStore: UserKeyStore,
    private val keyValueStorage: KeyValueStorageFacade,
    private val usersRepository: UsersRepository
) : LogoutUseCase {
    override suspend fun logout() {
        withContext(dispatchersProvider.ioDispatcher) {
            userKeyStore.clearAllKeys()

            keyValueStorage.removeAuthState()
            keyValueStorage.removePinCode()
            keyValueStorage.removeAppUnlockWay()
            keyValueStorage.removeLastUsedCommunityId()
            usersRepository.clearCurrentUserData()
        }
    }
}