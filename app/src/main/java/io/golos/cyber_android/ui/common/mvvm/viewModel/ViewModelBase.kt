package io.golos.cyber_android.ui.common.mvvm.viewModel

import androidx.annotation.CallSuper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.utils.toSingleLiveData
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

abstract class ViewModelBase<TModel: ModelBase>
constructor(
    dispatchersProvider: DispatchersProvider,
    _model: TModel? = null
) : ViewModel(), CoroutineScope {
    private val scopeJob: Job = SupervisorJob()

    @Suppress("UNCHECKED_CAST")
    protected val model: TModel = _model ?: ModelBaseImpl() as TModel

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        handleError(exception)
    }

    /**
     * Context of this scope.
     */
    override val coroutineContext: CoroutineContext = scopeJob + dispatchersProvider.uiDispatcher + errorHandler

    /**
     * Direct command for view
     */
    protected val commandMutableLiveData = MutableLiveData<ViewCommand>()

    val command = commandMutableLiveData.toSingleLiveData()

    /**
     * On configuration change we need to show dialog if it wasn't closed.
     * That's why we can't use [commandMutableLiveData]
     */
    val dialogCommands: MutableLiveData<ViewCommand> = MutableLiveData()

    @CallSuper
    override fun onCleared() {
        scopeJob.cancel()
        super.onCleared()
    }

    protected fun handleError(error: Throwable){
        Timber.e(error)
        commandMutableLiveData.value = ShowMessageCommand(R.string.loading_error)
    }
}