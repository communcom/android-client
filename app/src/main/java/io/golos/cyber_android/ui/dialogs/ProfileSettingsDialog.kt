package io.golos.cyber_android.ui.dialogs

import android.app.Activity
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import kotlinx.android.synthetic.main.dialog_profile_settings.*

class ProfileSettingsDialog : BottomSheetDialogFragmentBase() {
    companion object {
        const val REQUEST = 4053

        const val RESULT_LIKED = Activity.RESULT_FIRST_USER + 1
        const val RESULT_BLACK_LIST = Activity.RESULT_FIRST_USER + 2
        const val RESULT_LOGOUT = Activity.RESULT_FIRST_USER + 3
        const val RESULT_CANCEL = Activity.RESULT_FIRST_USER + 4

        fun newInstance(target: Fragment): ProfileSettingsDialog {
            return ProfileSettingsDialog().apply {
                setTargetFragment(target, REQUEST)
            }
        }
    }

    override fun provideLayout(): Int = R.layout.dialog_profile_settings

    override fun setupView() {
        liked.setSelectAction(RESULT_LIKED)
        blacklist.setSelectAction(RESULT_BLACK_LIST)
        logout.setSelectAction(RESULT_LOGOUT)
        closeButton.setSelectAction(RESULT_CANCEL)
    }
}