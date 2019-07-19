package io.golos.cyber_android.ui.screens.login.signup.keys_backup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.ui.common.mvvm.SingleLiveData
import io.golos.cyber_android.ui.common.mvvm.ViewCommand
import io.golos.cyber_android.ui.screens.login.signup.keys_backup.view_commands.NavigateToOnboardingCommand
import io.golos.cyber_android.ui.screens.login.signup.keys_backup.view_commands.StartExportingCommand
import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.UserKeyStore
import io.golos.domain.entities.UserKey
import io.golos.domain.entities.UserKeyType
import io.golos.sharedmodel.CyberName
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SignUpProtectionKeysViewModel(
    private val userKeyStore: UserKeyStore,
    private val keyValueStorage: KeyValueStorageFacade,
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

    fun onBackupCompleted() {
        launch {
            val newAuthState = keyValueStorage.getAuthState()!!.copy(isKeysExported = true)
            keyValueStorage.saveAuthState(newAuthState)

            command.value = NavigateToOnboardingCommand(getUser())
        }
    }

    fun onExportDialogCompleted(pathToSave: String) {
        launch {
            command.value = StartExportingCommand(pathToSave, getUser().name, getAllKeys())
        }
    }

    override fun onCleared() {
        scopeJob.takeIf { it.isActive }?.cancel()
    }

    private fun requestKeys() {
        launch {
            keysLiveData.postValue(getAllKeys())
        }
    }

    private suspend fun getAllKeys(): List<UserKey> =
        withContext(dispatchersProvider.ioDispatcher) {
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

    private suspend fun getUser(): CyberName =
        withContext(dispatchersProvider.ioDispatcher) {
            keyValueStorage.getAuthState()!!.user
        }
}