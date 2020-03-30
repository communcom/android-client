package io.golos.cyber_android.ui.screens.app_start.sign_in.app_unlock.model

import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.dto.AppUnlockWay
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class SignInAppUnlockModelImpl
@Inject
constructor(
    private val keyValueStorage: KeyValueStorageFacade,
    private val dispatchersProvider: DispatchersProvider):
    AppUnlockModel {

    /**
     * @return true in case of success
     */
    override suspend fun saveAppUnlockWay(unlockWay: AppUnlockWay) =
        withContext(dispatchersProvider.ioDispatcher) {
            try {
                keyValueStorage.saveAppUnlockWay(unlockWay)

                val newAuthState = keyValueStorage.getAuthState()!!.copy(isFingerprintSettingsPassed = true, isKeysExported = true)
                keyValueStorage.saveAuthState(newAuthState)
            } catch(ex: Exception) {
                Timber.e(ex)
                throw ex
            }
        }

    override suspend fun removeSignUpSnapshot() {
        withContext(dispatchersProvider.ioDispatcher) {
            try {
                keyValueStorage.removeSignUpSnapshot()
            } catch (ex: Exception) {
                Timber.e(ex)
            }
        }
    }
}