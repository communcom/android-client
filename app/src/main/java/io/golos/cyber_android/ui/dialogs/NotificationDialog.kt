package io.golos.cyber_android.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.Tags
import kotlinx.android.synthetic.main.dialog_notification.*

/**
 * Dialog that provides some notification to user
 */
class NotificationDialog: DialogFragment() {

    var listener: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_notification, container)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        message.text = arguments?.getString(Tags.MESSAGE)
        ok.setOnClickListener {
            listener?.invoke()
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.NotificationDialogStyle)
    }

    fun setOnOkClickListener(listener: (() -> Unit)?): NotificationDialog {
        this.listener = listener
        return this
    }

    companion object {
        fun newInstance(message: String) = NotificationDialog().apply {
            arguments = Bundle().apply {
                putString(Tags.MESSAGE, message)
            }
        }
    }
}