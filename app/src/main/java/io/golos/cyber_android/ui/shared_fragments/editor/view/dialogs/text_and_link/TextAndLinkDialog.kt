package io.golos.cyber_android.ui.shared_fragments.editor.view.dialogs.text_and_link

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import io.golos.cyber_android.R

class TextAndLinkDialog(
    private val context : Context,
    private val textToEdit: String,
    private val linkToEdit: String,
    @StringRes private val title: Int,
    private val resultCallback: (String?, String?) -> Unit) {

    //Select dialog's style - colorAccent for options and ? for buttons
    fun show() : AlertDialog {
        val dialog = AlertDialog
            .Builder(context, R.style.NotificationDialogStyle)
            .setTitle(title)
            .setCancelable(true)
            .also { dialog ->
                // Inflate view
                TextAndLinkDialogView(context)
                    .apply {
                        text = textToEdit
                        link = linkToEdit
                        dialog.setView(this)
                    }
            }
            .setPositiveButton(R.string.ok) { dialog, _ ->
                val dialogView = getView(dialog as AlertDialog)
                resultCallback(dialogView.text, dialogView.link)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                resultCallback(null, null)
            }
            .setOnCancelListener {
                resultCallback(null, null)
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

    private fun getView(dialog: AlertDialog): TextAndLinkDialogView =
        dialog.findViewById<View>(R.id.dialogTextAndLinkRoot)!!.parent as TextAndLinkDialogView

    private fun getOkButton(dialog: AlertDialog) = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
}