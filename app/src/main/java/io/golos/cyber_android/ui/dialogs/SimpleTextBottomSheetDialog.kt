package io.golos.cyber_android.ui.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import kotlinx.android.synthetic.main.dialog_simple_text.*


open class SimpleTextBottomSheetDialog : BottomSheetDialogFragmentBase() {
    companion object {
        const val REQUEST = 5634

        const val TITLE_KEY = "TITLE_KEY"
        const val TEXT_KEY = "TEXT_KEY"
        const val MAIN_BUTTON_TEXT_KEY = "MAIN_BUTTON_TEXT_KEY"

        const val RESULT_CANCEL = Activity.RESULT_FIRST_USER + 1
        const val RESULT_MAIN_ACTION = Activity.RESULT_FIRST_USER + 2

        fun newInstance(
            target: Fragment,
            @StringRes titleResId: Int,
            @StringRes textResId: Int,
            @StringRes mainButtonText: Int) =
            SimpleTextBottomSheetDialog().apply {
                arguments = Bundle().apply {
                    putInt(TITLE_KEY, titleResId)
                    putInt(TEXT_KEY, textResId)
                    putInt(MAIN_BUTTON_TEXT_KEY, mainButtonText)
                }
                setTargetFragment(target, REQUEST)
            }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val d = it as BottomSheetDialog

            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?

            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        return dialog
    }

    override fun provideLayout(): Int = R.layout.dialog_simple_text

    override fun setupView() {
        title.setText(arguments!!.getInt(TITLE_KEY))
        explanationText.setText(arguments!!.getInt(TEXT_KEY))
        mainButton.setText(arguments!!.getInt(MAIN_BUTTON_TEXT_KEY))

        closeButton.setSelectAction(RESULT_CANCEL)
        mainButton.setSelectAction(RESULT_MAIN_ACTION)
    }
}