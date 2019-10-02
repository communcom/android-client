package io.golos.cyber_android.ui.screens.in_app_auth_activity.navigation

import android.app.Activity
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.navigation.NavigatorBaseImpl
import io.golos.cyber_android.ui.screens.in_app_auth_activity.InAppAuthActivity
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.FingerprintAuthFragment
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.PinCodeAuthFragment
import javax.inject.Inject

class NavigatorImpl @Inject constructor() : NavigatorBaseImpl(R.id.authNavHost), Navigator {

    override fun setFingerprintAsHome(activity: FragmentActivity, @StringRes headerText: Int, isPinCodeUnlockEnabled: Boolean) =
        Bundle()
            .apply {
                putInt(InAppAuthActivity.FINGERPRINT_HEADER_ID, headerText)
                putBoolean(InAppAuthActivity.PIN_CODE_UNLOCK_ENABLED, isPinCodeUnlockEnabled)
            }
            .let { setHome(R.id.fingerprintAuthFragment, R.navigation.graph_in_app_auth, activity, it) }

    override fun setPinCodeAsHome(activity: FragmentActivity, @StringRes headerText: Int) =
        Bundle()
            .apply { putInt(InAppAuthActivity.PIN_CODE_HEADER_ID, headerText) }
            .let { setHome(R.id.pinCodeAuthFragment, R.navigation.graph_in_app_auth, activity, it) }

    override fun processAuthSuccess(activity: FragmentActivity) {
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }

    override fun setAuthFailResult(activity: FragmentActivity) {
        activity.setResult(Activity.RESULT_CANCELED)
    }

    override fun moveToFingerprint(fragment: PinCodeAuthFragment) =
        Bundle()
            .apply {
                putInt(
                    InAppAuthActivity.FINGERPRINT_HEADER_ID,
                    fragment.requireActivity().intent.extras!!.getInt(InAppAuthActivity.FINGERPRINT_HEADER_ID)
                )
            }
            .let {
                getNavigationController(fragment.requireActivity())
                    .navigate(R.id.action_pinCodeAuthFragment_to_fingerprintAuthFragment, it)
            }

    override fun moveToPinCode(fragment: FingerprintAuthFragment) =
        Bundle()
            .apply {
                putInt(
                    InAppAuthActivity.PIN_CODE_HEADER_ID,
                    fragment.requireActivity().intent.extras!!.getInt(InAppAuthActivity.PIN_CODE_HEADER_ID)
                )
            }
            .let {
                getNavigationController(fragment.requireActivity())
                    .navigate(R.id.action_fingerprintAuthFragment_to_pinCodeAuthFragment, it)
            }
}