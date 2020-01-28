package io.golos.cyber_android.ui.dialogs

import android.app.Activity
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import kotlinx.android.synthetic.main.dialog_user_name_restrictions.*

class UserNameRestrictionsWarningDialog : BottomSheetDialogFragmentBase() {
    companion object {
        const val REQUEST = 5634

        const val RESULT_CANCEL = Activity.RESULT_FIRST_USER + 2

        fun newInstance(target: Fragment): UserNameRestrictionsWarningDialog {
            return UserNameRestrictionsWarningDialog().apply {
                setTargetFragment(target, REQUEST)
            }
        }
    }

    override fun provideLayout(): Int = R.layout.dialog_user_name_restrictions

    override fun setupView() {
        understandButton.setSelectAction(RESULT_CANCEL)
        closeButton.setSelectAction(RESULT_CANCEL)
    }
}