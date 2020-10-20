package io.golos.cyber_android.ui.dialogs

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

open class SimpleTextBottomSheetDialog(
    @StringRes private val titleResId: Int,
    @StringRes private val textResId: Int,
    @StringRes private val mainButtonText: Int
) : BottomSheetDialogFragmentBase<SimpleTextBottomSheetDialog.Result>() {

    sealed class Result {
        object Ok: Result()
    }

    companion object {
        fun show(parent: Fragment,
                 @StringRes titleResId: Int,
                 @StringRes textResId: Int,
                 @StringRes mainButtonText: Int,
                 closeAction: (Result?) -> Unit) =
            SimpleTextBottomSheetDialog(titleResId, textResId, mainButtonText)
                .apply { closeActionListener = closeAction }
                .show(parent.parentFragmentManager, "SIMPLE_TEXT_BOTTOM_SHEET_DIALOG")
    }

    override val closeButton: View?
        get() = buttonClose

    override val layout: Int
        get() = R.layout.dialog_simple_text

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val d = it as BottomSheetDialog

            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?

            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        return dialog
    }

    override fun setupView() {
        title.setText(titleResId)
        explanationText.setText(textResId)
        mainButton.setText(mainButtonText)

        mainButton.setOnClickListener { closeOnItemSelected(Result.Ok) }
    }
}