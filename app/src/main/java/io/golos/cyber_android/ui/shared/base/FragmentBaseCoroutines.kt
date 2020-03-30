package io.golos.cyber_android.ui.shared.base

import androidx.fragment.app.Fragment
import io.golos.cyber_android.ui.shared.helper.UIHelper
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
abstract class FragmentBaseCoroutines: FragmentBase(), CoroutineScope {
    private val scopeJob: Job = SupervisorJob()

    @Inject
    protected lateinit var dispatchersProvider: DispatchersProvider

    /**
     * Context of this scope.
     */
    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    override fun onDestroy() {
        super.onDestroy()

        if(isRemoving) {
            scopeJob.cancel()
        }
    }
}