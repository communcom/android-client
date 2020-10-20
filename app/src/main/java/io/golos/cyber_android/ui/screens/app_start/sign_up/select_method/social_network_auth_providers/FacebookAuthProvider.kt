package io.golos.cyber_android.ui.screens.app_start.sign_up.select_method.social_network_auth_providers

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Base64
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import io.golos.cyber_android.BuildConfig
import io.golos.domain.analytics.AnalyticsFacade
import io.golos.use_cases.sign_up.core.SignUpCoreView
import io.golos.use_cases.sign_up.core.data_structs.TokenReceived
import io.golos.use_cases.sign_up.core.data_structs.TokenReceivingError
import timber.log.Timber
import java.security.MessageDigest
import javax.inject.Inject

class FacebookAuthProvider
@Inject
constructor(
    private val signUpCore: SignUpCoreView,
    private val analyticsFacade: AnalyticsFacade
) : SocialNetworkAuthProvider {
    companion object {
        // DO NOT REMOVE!!!
        @Suppress("unused")
        fun printKeyHash(context: Context) {
            val info: PackageInfo = context.packageManager.getPackageInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES)

            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Timber.tag("KEY_HASH").d(Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        }
    }

    private lateinit var facebookCallbackManager: CallbackManager

    override fun startAuth(fragment: Fragment) {
        val accessToken = AccessToken.getCurrentAccessToken()

        if(accessToken != null && !accessToken.isExpired) {
            processAccessToken(accessToken)
        } else {
            facebookCallbackManager = CallbackManager.Factory.create()

            LoginManager.getInstance().registerCallback(facebookCallbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    analyticsFacade.facebookAuth(true)
                    processAccessToken(result!!.accessToken)
                }

                override fun onCancel() {
                    analyticsFacade.facebookAuth(false)
                    signUpCore.process(TokenReceivingError())
                }

                override fun onError(error: FacebookException?) {
                    analyticsFacade.facebookAuth(false)
                    Timber.e(error)
                    signUpCore.process(TokenReceivingError())
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

        signUpCore.process(TokenReceived(token))
    }
}