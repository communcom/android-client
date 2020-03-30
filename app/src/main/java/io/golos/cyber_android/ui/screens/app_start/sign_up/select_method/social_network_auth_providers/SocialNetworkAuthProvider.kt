package io.golos.cyber_android.ui.screens.app_start.sign_up.select_method.social_network_auth_providers

import android.content.Intent
import androidx.fragment.app.Fragment

interface SocialNetworkAuthProvider {
    fun startAuth(fragment: Fragment)

    /**
     * @return true - processed
     */
    fun processActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean
}