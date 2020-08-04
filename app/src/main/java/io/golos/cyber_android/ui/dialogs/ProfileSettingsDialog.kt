package io.golos.cyber_android.ui.dialogs

import android.view.View
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import kotlinx.android.synthetic.main.dialog_profile_settings.*

class ProfileSettingsDialog : BottomSheetDialogFragmentBase<ProfileSettingsDialog.Result>() {
    sealed class Result {
        object Liked: Result()
        object BlackList: Result()
        object Notifications: Result()
        object SwitchTheme:Result()
        object Logout: Result()
    }

    private var isDarkModeEnabled:Boolean = false

    companion object {
        fun show(parent: Fragment,isDarkModeEnabled:Boolean, closeAction: (Result?) -> Unit) =
            ProfileSettingsDialog()
                .apply {
                    this.isDarkModeEnabled = isDarkModeEnabled
                    closeActionListener = closeAction
                }
                .show(parent.parentFragmentManager, "PROFILE_SETTINGS_DIALOG")
    }

    override val closeButton: View?
        get() = buttonClose

    override val layout: Int
        get() = R.layout.dialog_profile_settings

    override fun setupView() {
        isDarkModeEnabledSwitcher.isChecked = isDarkModeEnabled
        liked.setOnClickListener { closeOnItemSelected(Result.Liked) }
        blacklist.setOnClickListener { closeOnItemSelected(Result.BlackList) }
        notifications.setOnClickListener { closeOnItemSelected(Result.Notifications) }
        logout.setOnClickListener { closeOnItemSelected(Result.Logout) }
        isDarkModeEnabledSwitcher.setOnCheckedChangeListener { _, b ->  closeOnItemSelected(Result.SwitchTheme)}
    }
}