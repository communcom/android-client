package io.golos.cyber_android.ui.base

import androidx.fragment.app.Fragment
import io.golos.cyber_android.ui.dialogs.LoadingDialog

/**
 * [Fragment] that supports showing progress with [LoadingDialog] via
 * [showLoading] and [hideLoading] methods
 */
abstract class LoadingFragment: Fragment() {

    private val loadingDialog = LoadingDialog()

    protected fun showLoading() {
        if (loadingDialog.dialog?.isShowing != true && !loadingDialog.isAdded) {
            loadingDialog.show(requireFragmentManager(), "loading")
        }
    }

    protected fun hideLoading() {
        if (loadingDialog.fragmentManager != null)
            loadingDialog.dismiss()
    }
}