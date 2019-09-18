package io.golos.cyber_android.ui.shared_fragments.editor.dialogs.one_text_line

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
    fun show() : AlertDialog =
        AlertDialog
            .Builder(context, R.style.NotificationDialogStyle)
            .setTitle(title)
            .setCancelable(true)
            .also { dialog ->
                // Inflate view
                OneTextLineDialogView(context)
                    .apply {
                        text = textToEdit
                        dialog.setView(this)
                    }
            }
            .setPositiveButton(R.string.ok) { dialog, _ ->
                val dialogView = getView(dialog as AlertDialog)
                resultCallback(dialogView.text)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                resultCallback (null)
            }
            .setOnCancelListener {
                resultCallback(null)
            }
            .show()

    private fun getView(dialog: AlertDialog): OneTextLineDialogView =
        dialog.findViewById<View>(R.id.dialogOneTextLineRoot)!!.parent as OneTextLineDialogView
}