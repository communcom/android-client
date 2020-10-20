package io.golos.cyber_android.ui.screens.app_start.sign_up.select_method.social_network_auth_providers

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import io.golos.cyber_android.BuildConfig
import io.golos.domain.analytics.AnalyticsFacade
import io.golos.use_cases.sign_up.core.SignUpCoreView
import io.golos.use_cases.sign_up.core.data_structs.TokenReceived
import io.golos.use_cases.sign_up.core.data_structs.TokenReceivingError
import timber.log.Timber
import javax.inject.Inject

class GoogleAuthProvider
@Inject
constructor(
    private val signUpCore: SignUpCoreView,
    private val analyticsFacade: AnalyticsFacade
) : SocialNetworkAuthProvider {
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
            analyticsFacade.googleAuth(false)

            val status = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            Timber.w("Can't sign in on Google. The status is: ${status.status.statusMessage} [${status.status.statusCode}]")
            signUpCore.process(TokenReceivingError())
        }

        return true
    }

    private fun processSignInResult(data: Intent) {
        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
        if(result.isSuccess) {
            val account = result.signInAccount!!
            val idToken = account.idToken
            Timber.tag("ACCESS_TOKEN").d("accessToken: $idToken")

            analyticsFacade.googleAuth(true)

            signUpCore.process(TokenReceived(idToken!!))
        }
    }
}