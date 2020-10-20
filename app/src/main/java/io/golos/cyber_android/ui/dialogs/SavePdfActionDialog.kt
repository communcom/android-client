package io.golos.cyber_android.ui.dialogs

import android.view.View
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import kotlinx.android.synthetic.main.dialog_save_pdf.*

class SavePdfActionDialog : BottomSheetDialogFragmentBase<SavePdfActionDialog.Result>() {
    sealed class Result {
        object Device: Result()
        object GoogleDrive: Result()
    }

    companion object {
        fun show(parent: Fragment, closeAction: (Result?) -> Unit) =
            SavePdfActionDialog()
                .apply { closeActionListener = closeAction }
                .show(parent.parentFragmentManager, "SAVE_PDF_ACTION_DIALOG")
    }

    override val closeButton: View?
        get() = buttonClose

    override val layout: Int
        get() = R.layout.dialog_save_pdf

    override fun setupView() {
        device.setOnClickListener { closeOnItemSelected(Result.Device) }
        googleDrive.setOnClickListener { closeOnItemSelected(Result.GoogleDrive) }
    }
}