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
        object Logout: Result()
    }

    companion object {
        fun show(parent: Fragment, closeAction: (Result?) -> Unit) =
            ProfileSettingsDialog()
                .apply { closeActionListener = closeAction }
                .show(parent.parentFragmentManager, "PROFILE_SETTINGS_DIALOG")
    }

    override val closeButton: View?
        get() = buttonClose

    override val layout: Int
        get() = R.layout.dialog_profile_settings

    override fun setupView() {
        liked.setOnClickListener { closeOnItemSelected(Result.Liked) }
        blacklist.setOnClickListener { closeOnItemSelected(Result.BlackList) }
        logout.setOnClickListener { closeOnItemSelected(Result.Logout) }
    }
}