package io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.dto.ShowBackupWarningDialogCommand
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.dto.StartExportingKeyCommand
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.model.SignUpProtectionKeysModel
import io.golos.cyber_android.ui.shared.clipboard.ClipboardUtils
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.dto.UserKeyType
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SignUpProtectionKeysViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: SignUpProtectionKeysModel,
    private val keyValueStorage: KeyValueStorageFacade,
    private val clipboardUtils: ClipboardUtils
) : ViewModelBase<SignUpProtectionKeysModel>(dispatchersProvider, model) {

    private val _masterKey = MutableLiveData<String>()
    val masterKey get() = _masterKey as LiveData<String>

    init {
        launch {
            model.loadKeys()
            _masterKey.value = model.allKeys.first { it.keyType == UserKeyType.MASTER }.key
        }
    }

    fun onSavedClick() {
        _command.value = ShowBackupWarningDialogCommand()
    }

    fun onBackupCompleted() {
        launch {
            val newAuthState = keyValueStorage.getAuthState()!!.copy(isKeysExported = true)
            keyValueStorage.saveAuthState(newAuthState)

            _command.value = NavigateToMainScreenCommand()
        }
    }

    fun onWarningContinueClick() {
        launch {
            try {
                model.saveKeysExported()
                _command.value = NavigateToMainScreenCommand()
            } catch (ex: Exception) {
                _command.value = ShowMessageResCommand(R.string.common_general_error)
            }
        }
    }

    fun onExportPathSelected() {
        launch {
            _command.value = SetLoadingVisibilityCommand(true)

            try {
                _command.value = SetLoadingVisibilityCommand(false)
                _command.value = StartExportingKeyCommand(model.getDataForExporting())
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = SetLoadingVisibilityCommand(false)
                _command.value = ShowMessageResCommand(R.string.common_general_error)
            }
        }
    }

    fun onCopyMasterPasswordClick() {
        masterKey.value?.let {
            clipboardUtils.putPlainText(it)
            _command.value = ShowMessageResCommand(R.string.master_password_copied, isError = false)
        }
    }
}