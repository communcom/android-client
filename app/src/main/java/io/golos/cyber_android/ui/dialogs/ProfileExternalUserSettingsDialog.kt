package io.golos.cyber_android.ui.dialogs

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.dialog_profile_settings_external_user.*

class ProfileExternalUserSettingsDialog : BottomSheetDialogFragmentBase() {
    companion object {
        const val REQUEST = 4048

        private const val IS_BLOCKED = "IS_BLOCKED"

        const val RESULT_BLACK_LIST = Activity.RESULT_FIRST_USER + 1
        const val RESULT_CANCEL = Activity.RESULT_FIRST_USER + 2

        fun newInstance(target: Fragment, isBlocked: Boolean): ProfileExternalUserSettingsDialog {
            return ProfileExternalUserSettingsDialog().apply {
                setTargetFragment(target, REQUEST)
                arguments = Bundle().apply {
                    putBoolean(IS_BLOCKED, isBlocked)
                }
            }
        }
    }

    override fun provideLayout(): Int = R.layout.dialog_profile_settings_external_user

    override fun setupView() {
        if(arguments!!.getBoolean(IS_BLOCKED)) {
            blockUser.setText(R.string.unblock_user)

        } else {
            blockUser.setText(R.string.block_user)
        }

        blockUser.setSelectAction(RESULT_BLACK_LIST)
        closeButton.setSelectAction(RESULT_CANCEL)
    }
}