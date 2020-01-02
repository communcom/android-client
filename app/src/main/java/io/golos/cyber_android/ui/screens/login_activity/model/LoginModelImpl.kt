package io.golos.cyber_android.ui.screens.login_activity.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.data.network_state.NetworkStateChecker
import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.UserKeyStore
import io.golos.domain.dto.AuthStateDomain
import io.golos.domain.dto.UserKeyType
import io.golos.use_cases.auth.AuthUseCase
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.system.exitProcess

class LoginModelImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val authUseCase: AuthUseCase,
    private val userKeyStore: UserKeyStore,
    private val keyValueStorage: KeyValueStorageFacade,
    private val networkStateChecker: NetworkStateChecker
) : ModelBaseImpl(),
    LoginModel {

    private lateinit var authState: AuthStateDomain

    override val hasNetworkConnection: Boolean
        get() = networkStateChecker.isConnected

    override val isSetupCompleted: Boolean
        get() = authState.isKeysExported

    override suspend fun hasAuthState(): Boolean =
        withContext(dispatchersProvider.ioDispatcher) {
            keyValueStorage.getAuthState()
                ?.let {
                    authState = it
                    true
                } ?: false
        }

    override fun closeApp() = exitProcess(0)

    override suspend fun login(): Boolean =
        try {
            val masterPassword = withContext(dispatchersProvider.ioDispatcher) {
                userKeyStore.getKey(UserKeyType.MASTER)
            }

            authUseCase.auth(authState.userName, masterPassword)

            true
        } catch (ex: Exception) {
            Timber.e(ex)
            false
        }
}