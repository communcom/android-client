package io.golos.cyber_android.ui.dialogs

import android.view.View
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import kotlinx.android.synthetic.main.dialog_profile_settings_external_user.*

class ProfileExternalUserSettingsDialog(
    private val isBlocked: Boolean
) : BottomSheetDialogFragmentBase<ProfileExternalUserSettingsDialog.Result>() {

    sealed class Result {
        object BlackList: Result()
    }

    companion object {
        fun show(parent: Fragment, isBlocked: Boolean, closeAction: (Result?) -> Unit) =
            ProfileExternalUserSettingsDialog(isBlocked)
                .apply { closeActionListener = closeAction }
                .show(parent.parentFragmentManager, "PROFILE_EXTERNAL_USER_SETTINGS_DIALOG")
    }

    override val closeButton: View?
        get() = buttonClose

    override val layout: Int
        get() = R.layout.dialog_profile_settings_external_user

    override fun setupView() {
        blockUser.setText(if(isBlocked) R.string.unblock_user else R.string.block_user)

        blockUser.setOnClickListener { closeOnItemSelected(Result.BlackList) }
    }
}