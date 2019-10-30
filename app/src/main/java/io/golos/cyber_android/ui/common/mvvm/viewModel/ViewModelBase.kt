package io.golos.cyber_android.ui.common.mvvm.viewModel

import androidx.annotation.CallSuper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.ui.common.mvvm.SingleLiveData
import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

abstract class ViewModelBase<TModel : ModelBase>
constructor(
    private val dispatchersProvider: DispatchersProvider,
    protected val model: TModel
) : ViewModel(), CoroutineScope {
    private val scopeJob: Job = SupervisorJob()

    /**
     * Context of this scope.
     */
    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    /**
     * Direct command for view
     */
    val command: SingleLiveData<ViewCommand> = SingleLiveData()

    /**
     * On configuration change we need to show dialog if it wasn't closed.
     * That's why we can't use [command]
     */
    val dialogCommands: MutableLiveData<ViewCommand> = MutableLiveData()

    @CallSuper
    override fun onCleared() {
        super.onCleared()

        scopeJob.cancelChildren()
        scopeJob.cancel()
    }
}