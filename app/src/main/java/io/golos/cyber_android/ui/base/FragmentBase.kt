package io.golos.cyber_android.ui.base

import androidx.fragment.app.Fragment
import io.golos.cyber_android.ui.common.helper.UIHelper
import io.golos.cyber_android.ui.dialogs.LoadingDialog
import javax.inject.Inject

/**
 * [Fragment] that supports showing progress with [LoadingDialog] via
 * [showLoading] and [hideLoading] methods
 */
abstract class FragmentBase: Fragment() {

    private val loadingDialog = LoadingDialog()

    private var wasAdded = false

    @Inject
    protected lateinit var uiHelper: UIHelper

    protected fun setLoadingVisibility(isVisible: Boolean) =
        if(isVisible) {
            showLoading()
        } else {
            hideLoading()
        }

    protected fun showLoading() {
        if (loadingDialog.dialog?.isShowing != true && !loadingDialog.isAdded && !wasAdded) {
            loadingDialog.show(requireFragmentManager(), "loading")
            wasAdded = true
        }
    }

    protected fun hideLoading() {
        if (loadingDialog.fragmentManager != null && wasAdded) {
            loadingDialog.dismiss()
            wasAdded = false
        }
    }
}