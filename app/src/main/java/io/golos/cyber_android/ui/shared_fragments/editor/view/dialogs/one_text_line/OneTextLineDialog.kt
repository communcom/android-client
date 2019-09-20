package io.golos.cyber_android.ui.shared_fragments.editor.view.dialogs.one_text_line

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import io.golos.cyber_android.R

class OneTextLineDialog(
    private val context : Context,
    private val textToEdit: String,
    @StringRes private val title: Int,
    private val resultCallback: (String?) -> Unit) {

    //Select dialog's style - colorAccent for options and ? for buttons
    fun show() : AlertDialog {
        val dialog = AlertDialog
            .Builder(context, R.style.NotificationDialogStyle)
            .setTitle(title)
            .setCancelable(true)
            .also { builder ->
                // Inflate view
                OneTextLineDialogView(context)
                    .apply {
                        text = textToEdit
                        builder.setView(this)
                    }
            }
            .setPositiveButton(R.string.ok) { dialog, _ ->
                val dialogView = getView(dialog as AlertDialog)
                resultCallback(dialogView.text)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                resultCallback(null)
            }
            .setOnCancelListener {
                resultCallback(null)
            }
            .create()

        dialog.show()

        val view = getView(dialog)

        getOkButton(dialog).isEnabled = !view.isTextEmpty
        view.setTextIsEmptyListener { textIsEmpty ->
            getOkButton(dialog).isEnabled = !textIsEmpty
        }

        return dialog
    }

    private fun getView(dialog: AlertDialog): OneTextLineDialogView =
        dialog.findViewById<View>(R.id.dialogOneTextLineRoot)!!.parent as OneTextLineDialogView

    private fun getOkButton(dialog: AlertDialog) = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
}