package io.golos.cyber_android.ui.screens.login_sign_up_select_method.social_network_auth_providers

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.shared.mvvm.SingleLiveData
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.data.repositories.sign_up_tokens.SignUpTokensRepository
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
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
}