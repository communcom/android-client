package io.golos.cyber_android.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.Tags
import kotlinx.android.synthetic.main.dialog_confirmation.*

/**
 * Dialog that asks for some confirmation from user.
 */
class ConfirmationDialog: DialogFragment() {

    var listener: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_confirmation, container)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        message.text = arguments?.getString(Tags.MESSAGE)
        ok.setOnClickListener {
            listener?.invoke()
            dismiss()
        }
        cancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.NotificationDialogStyle)
    }

    companion object {
        fun newInstance(message: String) = ConfirmationDialog().apply {
            arguments = Bundle().apply {
                putString(Tags.MESSAGE, message)
            }
        }
    }
}