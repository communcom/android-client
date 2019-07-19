package io.golos.cyber_android.ui.screens.login.signup.keys

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.ui.common.mvvm.SingleLiveData
import io.golos.cyber_android.ui.common.mvvm.ViewCommand
import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.UserKeyStore
import io.golos.domain.entities.UserKey
import io.golos.domain.entities.UserKeyType
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SignUpProtectionKeysViewModel(
    private val userKeyStore: UserKeyStore,
    private val keyValueStorageFacade: KeyValueStorageFacade,
    private val dispatchersProvider: DispatchersProvider
) : ViewModel(), CoroutineScope {

    private val scopeJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    val keysLiveData = MutableLiveData<List<UserKey>>()

    val command: SingleLiveData<ViewCommand> = SingleLiveData()

    init {
        requestKeys()
    }

    fun backupCompleted() {
        launch {
            val user = withContext(dispatchersProvider.ioDispatcher) {
                keyValueStorageFacade.getAuthState()!!.user
            }
            command.value = NavigateToOnboardingCommand(user)
        }
    }

    override fun onCleared() {
        scopeJob.takeIf { it.isActive }?.cancel()
    }

    private fun requestKeys() {
        launch {
            val keys = withContext(dispatchersProvider.ioDispatcher) {
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
            keysLiveData.postValue(keys)
        }
    }
}