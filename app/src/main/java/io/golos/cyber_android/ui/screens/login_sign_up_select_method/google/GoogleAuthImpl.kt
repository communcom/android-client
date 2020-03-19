package io.golos.cyber_android.ui.screens.login_sign_up_select_method.google

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.mvvm.SingleLiveData
import io.golos.cyber_android.ui.shared.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.data.repositories.sign_up_tokens.SignUpTokensRepository
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class GoogleAuthImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val signUpTokensRepository: SignUpTokensRepository
) : GoogleAuth, CoroutineScope {

    private val scopeJob: Job = SupervisorJob()

    private val requestCode = 40955

    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    private val _command = SingleLiveData<ViewCommand>()
    override val command: LiveData<ViewCommand> = _command

    override fun startAuth(fragment: Fragment) {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestServerAuthCode(BuildConfig.GOOGLE_ACCESS_TOKEN_CLIENT_ID)
            .build()

        val client = GoogleSignIn.getClient(fragment.requireActivity(), signInOptions)
        fragment.startActivityForResult(client.signInIntent, requestCode)
    }

    /**
     * @return true - processed
     */
    override fun processActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if(requestCode != this.requestCode) {
            return false
        }

        if(resultCode == Activity.RESULT_OK) {
            processSignInResult(data!!)
        } else {
            Timber.w("Can't sign in on Google")
            _command.value = ShowMessageResCommand(R.string.common_general_error)
        }

        return true
    }

    override fun close() {
        scopeJob.takeIf { it.isActive }?.cancel()
    }

    private fun processSignInResult(data: Intent) {
        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
        if(result.isSuccess) {
            val account = result.signInAccount!!
            val authCode = account.serverAuthCode
            Timber.tag("ACCESS_TOKEN").d("authCode: $authCode")

            launch {
                _command.value = SetLoadingVisibilityCommand(true)

                try {
                    val accessToken = signUpTokensRepository.getGoogleAccessToken(authCode!!)
                    Timber.tag("ACCESS_TOKEN").d("accessToken: $accessToken")
                    val identity = signUpTokensRepository.getGoogleIdentity(accessToken)
                    Timber.tag("ACCESS_TOKEN").d("identity: $identity")
                } catch (ex: Exception) {
                    Timber.e(ex)
                    _command.value = ShowMessageResCommand(R.string.common_general_error)
                } finally {
                    _command.value = SetLoadingVisibilityCommand(false)
                }
            }
        }
    }
}