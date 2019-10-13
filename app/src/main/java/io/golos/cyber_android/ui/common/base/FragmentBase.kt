package io.golos.cyber_android.ui.common.base

import androidx.fragment.app.Fragment
import io.golos.cyber_android.ui.common.helper.UIHelper
import io.golos.cyber_android.ui.dialogs.LoadingDialog
import io.golos.domain.DispatchersProvider
import io.golos.domain.LogTags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * [Fragment] that supports showing progress with [LoadingDialog] via
 * [showLoading] and [hideLoading] methods
 */
abstract class FragmentBase: Fragment(), CoroutineScope {

    private val scopeJob: Job = SupervisorJob()

    private val loadingDialog = LoadingDialog()

    private var wasAdded = false

    @Inject
    protected lateinit var uiHelper: UIHelper

    @Inject
    protected lateinit var dispatchersProvider: DispatchersProvider

    /**
     * Context of this scope.
     */
    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    override fun onResume() {
        super.onResume()
        Timber.tag(LogTags.NAVIGATION).d("${javaClass.simpleName} fragment is active")
    }

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