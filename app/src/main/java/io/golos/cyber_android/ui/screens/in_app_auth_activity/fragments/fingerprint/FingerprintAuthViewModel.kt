package io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.core.fingerprints.eventsHandler.*
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.model.FingerprintAuthModel
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.AuthSuccessCommand
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.SwitchToPinCodeCommand
import io.golos.domain.AppResourcesProvider
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class FingerprintAuthViewModel
@Inject
constructor(
    @StringRes private val headerText: Int,
    private val appResourcesProvider: AppResourcesProvider,
    dispatchersProvider: DispatchersProvider,
    model: FingerprintAuthModel
) : ViewModelBase<FingerprintAuthModel>(dispatchersProvider, model) {

    private val fingerprintAuthEventHandler: FingerprintAuthEventHandler = { processAuthEvents(it) }

    private val normalColor = appResourcesProvider.getColor(R.color.black)
    private val errorColor by lazy { appResourcesProvider.getColor(R.color.red) }

    val title: MutableLiveData<String> = MutableLiveData(appResourcesProvider.getString(headerText))

    val messageText: MutableLiveData<String> = MutableLiveData(appResourcesProvider.getString(R.string.authTouchSensor))
    val messageColor: MutableLiveData<Int> = MutableLiveData(normalColor)

    fun onActive() {
        model.startAuth(fingerprintAuthEventHandler)

        messageText.value = appResourcesProvider.getString(R.string.authTouchSensor)
        messageColor.value = normalColor
    }

    fun onInactive() {
        model.cancelAuth()
    }

    fun onSwitchToPinCodeClick() {
        command.value = SwitchToPinCodeCommand()
    }

    private fun processAuthEvents(event: FingerprintAuthEvent) {
        when (event) {
            is FingerprintAuthSuccessEvent -> command.value = AuthSuccessCommand()

            is FingerprintAuthFailEvent -> {
                messageText.value = appResourcesProvider.getString(R.string.authFingerprintAuthFail)
                messageColor.value = errorColor
            }

            is FingerprintAuthErrorEvent -> event.message?.let { message ->
                messageText.value = message
                messageColor.value = errorColor
            }

            is FingerprintAuthWarningEvent -> event.message?.let { message ->
                messageText.value = message
                messageColor.value = normalColor
            }
        }
    }
}