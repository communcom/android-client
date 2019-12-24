package io.golos.cyber_android.ui.dialogs

import android.app.Activity
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.DialogFragmentBase
import kotlinx.android.synthetic.main.dialog_keys_backup_warning.*

class KeysBackupWarningDialog : DialogFragmentBase() {
    companion object {
        const val REQUEST = 8053

        const val RESULT_CONTINUE = Activity.RESULT_FIRST_USER + 1
        const val RESULT_CANCEL = Activity.RESULT_FIRST_USER + 2

        fun newInstance(target: Fragment): KeysBackupWarningDialog {
            return KeysBackupWarningDialog().apply {
                setTargetFragment(target, REQUEST)
            }
        }
    }

    override fun provideLayout(): Int = R.layout.dialog_keys_backup_warning

    override fun setupView() {
        backupButton.setSelectAction(RESULT_CANCEL)
        continueButton.setSelectAction(RESULT_CONTINUE)
        closeButton.setSelectAction(RESULT_CANCEL)
    }
}