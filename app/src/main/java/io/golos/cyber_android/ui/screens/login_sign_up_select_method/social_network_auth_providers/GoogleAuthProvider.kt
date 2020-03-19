package io.golos.cyber_android.ui.screens.login_sign_up_select_method.social_network_auth_providers

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.data.repositories.sign_up_tokens.SignUpTokensRepository
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class GoogleAuthProvider
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    signUpTokensRepository: SignUpTokensRepository
) : AuthProviderBase(
    dispatchersProvider,
    signUpTokensRepository
) {
    private val requestCode = 40955

    override fun startAuth(fragment: Fragment) {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(BuildConfig.GOOGLE_ACCESS_TOKEN_CLIENT_ID)
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
            val status = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            Timber.w("Can't sign in on Google. The status is: ${status.status.statusMessage} [${status.status.statusCode}]")
            _command.value = ShowMessageResCommand(R.string.common_error_operation_canceled)
        }

        return true
    }

    private fun processSignInResult(data: Intent) {
        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
        if(result.isSuccess) {
            val account = result.signInAccount!!
            val idToken = account.idToken

            launch {
                _command.value = SetLoadingVisibilityCommand(true)

                try {
                    Timber.tag("ACCESS_TOKEN").d("accessToken: $idToken")
                    val identity = signUpTokensRepository.getGoogleIdentity(idToken!!)
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