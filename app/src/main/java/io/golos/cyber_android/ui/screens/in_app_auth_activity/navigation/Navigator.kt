package io.golos.cyber_android.ui.screens.in_app_auth_activity.navigation

import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.FingerprintAuthFragment
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.PinCodeAuthFragment
import io.golos.cyber_android.ui.shared.navigation.NavigatorBase

interface Navigator: NavigatorBase {
    fun setFingerprintAsHome(activity: FragmentActivity, @StringRes headerText: Int, isPinCodeUnlockEnabled: Boolean)

    fun setPinCodeAsHome(activity: FragmentActivity, @StringRes headerText: Int)

    fun processAuthSuccess(activity: FragmentActivity)

    fun setAuthFailResult(activity: FragmentActivity)

    fun moveToFingerprint(fragment: PinCodeAuthFragment)

    fun moveToPinCode(fragment: FingerprintAuthFragment)
}