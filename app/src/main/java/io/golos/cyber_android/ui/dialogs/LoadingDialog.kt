package io.golos.cyber_android.ui.dialogs

import androidx.fragment.app.DialogFragment
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import io.golos.cyber_android.R

/**
 * Undissmisable dialog which displays only Progress bar
 */
class LoadingDialog: DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_loading, container)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isCancelable = false
    }
}