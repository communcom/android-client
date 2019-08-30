package io.golos.cyber_android.ui.screens.login_activity.signup.fragments.keys_backup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber4j.services.model.UserMetadataResult
import io.golos.cyber4j.sharedmodel.CyberName
import io.golos.cyber_android.R
import io.golos.cyber_android.core.keys_backup.facade.BackupKeysFacade
import io.golos.cyber_android.ui.common.mvvm.SingleLiveData
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.keys_backup.view_commands.NavigateToOnboardingCommand
import io.golos.cyber_android.ui.common.keys_to_pdf.StartExportingCommand
import io.golos.data.api.UserMetadataApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.Logger
import io.golos.domain.UserKeyStore
import io.golos.domain.entities.UserKey
import io.golos.domain.entities.UserKeyType
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SignUpProtectionKeysViewModel
@Inject
constructor(
    private val userKeyStore: UserKeyStore,
    private val keyValueStorage: KeyValueStorageFacade,
    private val dispatchersProvider: DispatchersProvider,
    private val metadadataApi: UserMetadataApi,
    private val logger: Logger,
    private val backupKeysFacade: BackupKeysFacade
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

    fun onExportPathSelected() {
        launch {
            command.value = SetLoadingVisibilityCommand(true)

            try {
                val metadata = getUserMetadata(getUser())
                val keys = getAllKeys()

                command.value = SetLoadingVisibilityCommand(false)
                command.value = StartExportingCommand(
                    metadata.username,
                    metadata.userId.name,
                    keys
                )
            } catch (ex: Exception) {
                logger.log(ex)
                command.value = SetLoadingVisibilityCommand(false)
                command.value = ShowMessageCommand(R.string.common_general_error)
            }
        }
    }

    override fun onCleared() {
        scopeJob.takeIf { it.isActive }?.cancel()
    }

    private fun requestKeys() {
        launch {
            val allKeys = getAllKeys()

            keysLiveData.postValue(allKeys)     // Show keys on UI

            // Backup keys to the cloud
            val masterKey = allKeys.single { it.keyType == UserKeyType.MASTER }.key
            val userName = getUserMetadata(getUser()).username

            backupKeysFacade.putKey(userName, masterKey)
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

    private suspend fun getUserMetadata(user: CyberName): UserMetadataResult =
        withContext(dispatchersProvider.ioDispatcher) {
            metadadataApi.getUserMetadata(user)
        }
}