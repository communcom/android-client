package io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.UserKeyStore
import io.golos.domain.dto.UserKey
import io.golos.domain.dto.UserKeyType
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class SignUpProtectionKeysModelImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val userKeyStore: UserKeyStore,
    private val keyValueStorage: KeyValueStorageFacade
) : ModelBaseImpl(), SignUpProtectionKeysModel {

    private lateinit var _allKeys: List<UserKey>
    override val allKeys: List<UserKey>
        get() = _allKeys

    override suspend fun loadKeys() {
        _allKeys = withContext(dispatchersProvider.ioDispatcher) {
            listOf(
                UserKeyType.MASTER,
                UserKeyType.OWNER,
                UserKeyType.ACTIVE,
                UserKeyType.POSTING,
                UserKeyType.MEMO
            )
                .map { keyType ->
                    UserKey(keyType, userKeyStore.getKey(keyType))
                }
        }
    }

    override suspend fun saveKeysExported() {
        withContext(dispatchersProvider.ioDispatcher) {
            try {
                val newAuthState = keyValueStorage.getAuthState()!!.copy(isKeysExported = true)
                keyValueStorage.saveAuthState(newAuthState)
            } catch(ex: Exception) {
                Timber.e(ex)
                throw ex
            }
        }
    }
}