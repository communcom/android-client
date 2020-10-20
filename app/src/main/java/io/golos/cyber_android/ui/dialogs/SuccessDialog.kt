package io.golos.cyber_android.ui.dialogs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.Tags
import kotlinx.android.synthetic.main.dialog_success.*

/**
 * Dialog that asks for some confirmation from user.
 */
class SuccessDialog: DialogFragment() {
    companion object {
        const val REQUEST = 47896

        const val RESULT_OK = Activity.RESULT_FIRST_USER + 1

        fun newInstance(@StringRes messageResId: Int, target: Fragment) = SuccessDialog().apply {
            arguments = Bundle().apply {
                putInt(Tags.MESSAGE, messageResId)
                setTargetFragment(target, REQUEST)
            }
        }

        fun newInstance(message: String, target: Fragment) = SuccessDialog().apply {
            arguments = Bundle().apply {
                putString(Tags.MESSAGE_EDITABLE, message)
                setTargetFragment(target, REQUEST)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.NotificationDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_success, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments!!.getString(Tags.MESSAGE_EDITABLE) != null){
            message.text = arguments!!.getString(Tags.MESSAGE_EDITABLE)
        } else
            message.setText(arguments!!.getInt(Tags.MESSAGE))
        ok.setSelectAction(RESULT_OK)
    }

    private fun View.setSelectAction(resultCode: Int) {
        this.setOnClickListener {
            targetFragment?.onActivityResult(targetRequestCode, resultCode, Intent())
            dismiss()
        }
    }
}