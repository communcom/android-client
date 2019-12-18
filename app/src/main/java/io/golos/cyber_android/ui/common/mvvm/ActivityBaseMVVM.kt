package io.golos.cyber_android.ui.common.mvvm

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.golos.cyber_android.ui.common.helper.UIHelper
import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.dialogs.LoadingDialog
import io.golos.domain.LogTags
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Base class for all activities
 */
abstract class ActivityBaseMVVM<VDB : ViewDataBinding, VM : ViewModelBase<out ModelBase>> : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: VDB

    private lateinit var _viewModel: VM

    protected val viewModel: VM
        get() = _viewModel

    private var loadingDialog: LoadingDialog? = null

    @Inject
    internal lateinit var uiHelper: UIHelper

    @Inject
    internal lateinit var viewModelFactory: ActivityViewModelFactory

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main + errorHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, this.layoutResId())
        binding.lifecycleOwner = this
        inject()
        val viewModel = ViewModelProviders.of(this, viewModelFactory)[provideViewModelType()]
        _viewModel = viewModel
        linkViewModel(binding, _viewModel)
        _viewModel.command.observe(this, Observer {
            if (!processViewCommandGeneral(it)) {
                processViewCommand(it)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Timber.tag(LogTags.NAVIGATION).d("${javaClass.simpleName} fragment is active")
    }

    override fun onDestroy() {
        releaseInjection()
        coroutineContext.cancel()
        super.onDestroy()
    }

    abstract fun provideViewModelType(): Class<VM>

    @LayoutRes
    protected abstract fun layoutResId(): Int

    protected abstract fun inject()

    protected abstract fun releaseInjection()

    protected abstract fun linkViewModel(binding: VDB, viewModel: VM)

    protected open fun processViewCommand(command: ViewCommand) {}

    /**
     * Process input _command
     * @return true if the _command has been processed
     */
    private fun processViewCommandGeneral(command: ViewCommand): Boolean =
        when (command) {
            is ShowMessageResCommand -> {
                uiHelper.showMessage(command.textResId)
                true
            }
            is SetLoadingVisibilityCommand -> {
                setBlockingLoadingProgressVisibility(command.isVisible)
                true
            }
            else -> false
        }

    private fun setBlockingLoadingProgressVisibility(isVisible: Boolean) {
        if (isVisible) {
            if (loadingDialog == null) {
                loadingDialog = LoadingDialog()
                loadingDialog?.show(supportFragmentManager, LoadingDialog::class.java.name)
            }
        } else {
            if (loadingDialog != null) {
                loadingDialog?.dismiss()
                loadingDialog = null
            }
        }
    }
}