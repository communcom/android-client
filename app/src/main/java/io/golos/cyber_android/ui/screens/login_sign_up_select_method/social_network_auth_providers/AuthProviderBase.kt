package io.golos.cyber_android.ui.screens.login_sign_up_select_method.social_network_auth_providers

import androidx.lifecycle.LiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.login_sign_up_select_method.dto.NavigateToUserNameStepCommand
import io.golos.cyber_android.ui.shared.mvvm.SingleLiveData
import io.golos.cyber_android.ui.shared.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.data.repositories.sign_up_tokens.SignUpTokensRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.SignUpIdentityDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

abstract class AuthProviderBase
constructor(
    protected val dispatchersProvider: DispatchersProvider,
    protected val signUpTokensRepository: SignUpTokensRepository
) : SocialNetworkAuthProvider,
    CoroutineScope {

    private val scopeJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    protected val _command = SingleLiveData<ViewCommand>()
    override val command: LiveData<ViewCommand> = _command


    override fun close() {
        scopeJob.takeIf { it.isActive }?.cancel()
    }

    abstract suspend fun getIdentity(accessToken: String): SignUpIdentityDomain

    protected fun processToken(accessToken: String) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)

                val identity = getIdentity(accessToken)
                Timber.tag("ACCESS_TOKEN").d("identity: $identity")

                _command.value = SetLoadingVisibilityCommand(false)

                _command.value = if(identity.identity == null) {
                    if(identity.oauthState=="registered") {
                        ShowMessageResCommand(R.string.common_error_account_registered)
                    } else {
                        ShowMessageResCommand(R.string.common_general_error)
                    }
                } else {
                    NavigateToUserNameStepCommand(identity)
                }
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = SetLoadingVisibilityCommand(false)
                _command.value = ShowMessageResCommand(R.string.common_general_error)
            }
        }
    }
}