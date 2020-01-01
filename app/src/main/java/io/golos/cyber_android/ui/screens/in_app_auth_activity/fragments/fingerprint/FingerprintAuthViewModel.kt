package io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.application.shared.fingerprints.eventsHandler.*
import io.golos.cyber_android.ui.shared.extensions.getColorRes
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.model.FingerprintAuthModel
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.AuthSuccessCommand
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.SwitchToPinCodeCommand
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class FingerprintAuthViewModel
@Inject
constructor(
    private val context: Context,
    @StringRes private val headerText: Int,
    dispatchersProvider: DispatchersProvider,
    model: FingerprintAuthModel
) : ViewModelBase<FingerprintAuthModel>(dispatchersProvider, model) {

    private val fingerprintAuthEventHandler: FingerprintAuthEventHandler = { processAuthEvents(it) }

    private val normalColor = context.resources.getColorRes(R.color.black)
    private val errorColor by lazy { context.resources.getColorRes(R.color.red) }

    val title: MutableLiveData<String> = MutableLiveData(context.resources.getString(headerText))

    val messageText: MutableLiveData<String> = MutableLiveData(context.resources.getString(R.string.authTouchSensor))
    val messageColor: MutableLiveData<Int> = MutableLiveData(normalColor)

    fun onActive() {
        model.startAuth(fingerprintAuthEventHandler)

        messageText.value = context.resources.getString(R.string.authTouchSensor)
        messageColor.value = normalColor
    }

    fun onInactive() {
        model.cancelAuth()
    }

    fun onSwitchToPinCodeClick() {
        _command.value = SwitchToPinCodeCommand()
    }

    private fun processAuthEvents(event: FingerprintAuthEvent) {
        when (event) {
            is FingerprintAuthSuccessEvent -> _command.value = AuthSuccessCommand()

            is FingerprintAuthFailEvent -> {
                messageText.value = context.resources.getString(R.string.authFingerprintAuthFail)
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