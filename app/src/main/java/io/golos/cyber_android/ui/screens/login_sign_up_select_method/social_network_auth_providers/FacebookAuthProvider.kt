package io.golos.cyber_android.ui.screens.login_sign_up_select_method.social_network_auth_providers

import android.content.Intent
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.login_sign_up_select_method.dto.NavigateToUserNameStepCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.data.repositories.sign_up_tokens.SignUpTokensRepository
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class FacebookAuthProvider
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    signUpTokensRepository: SignUpTokensRepository
) : AuthProviderBase(
    dispatchersProvider,
    signUpTokensRepository
) {
    private lateinit var facebookCallbackManager: CallbackManager

    override fun startAuth(fragment: Fragment) {
        val accessToken = AccessToken.getCurrentAccessToken()

        if(accessToken != null && !accessToken.isExpired) {
            processAccessToken(accessToken)
        } else {
            facebookCallbackManager = CallbackManager.Factory.create()

            LoginManager.getInstance().registerCallback(facebookCallbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    processAccessToken(result!!.accessToken)
                }

                override fun onCancel() {
                    _command.value = ShowMessageResCommand(R.string.common_error_operation_canceled)
                }

                override fun onError(error: FacebookException?) {
                    Timber.e(error)
                    _command.value = ShowMessageResCommand(R.string.common_general_error)
                }
            })

            LoginManager.getInstance().logInWithReadPermissions(fragment, listOf("public_profile"))
        }
    }

    /**
     * @return true - processed
     */
    override fun processActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data)
        return true
    }

    private fun processAccessToken(accessToken: AccessToken) {
        val token = accessToken.token
        Timber.tag("ACCESS_TOKEN").d("accessToken: $token")

        launch {
            _command.value = SetLoadingVisibilityCommand(true)

            try {
                val identity = signUpTokensRepository.getFacebookIdentity(token)
                Timber.tag("ACCESS_TOKEN").d("identity: $identity")

                _command.value = SetLoadingVisibilityCommand(false)
                _command.value = NavigateToUserNameStepCommand(identity)
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = SetLoadingVisibilityCommand(false)
                _command.value = ShowMessageResCommand(R.string.common_general_error)

            }
        }
    }
}