package io.golos.cyber_android.ui.common.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import io.golos.cyber_android.ui.common.helper.UIHelper
import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.dialogs.LoadingDialog
import io.golos.cyber_android.ui.screens.dashboard.view.DashboardFragment
import io.golos.domain.LogTags
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Base class for all fragments
 */
abstract class FragmentBaseMVVM<VDB: ViewDataBinding, VM: ViewModelBase<out ModelBase>> : Fragment(), CoroutineScope {

    private lateinit var binding: VDB

    private lateinit var _viewModel: VM

    protected val viewModel: VM
        get() = _viewModel

    private var loadingDialog: LoadingDialog? = null

    @Inject
    internal lateinit var uiHelper: UIHelper

    @Inject
    internal lateinit var viewModelFactory: FragmentViewModelFactory

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main + errorHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        val viewModel = ViewModelProviders.of(this, viewModelFactory)[provideViewModelType()]
        _viewModel = viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _viewModel.command.observe({viewLifecycleOwner.lifecycle}) {
            if(!processViewCommandGeneral(it)) {
                processViewCommand(it)
            }
        }

        binding = DataBindingUtil.inflate(inflater, this.layoutResId(), container, false)
        binding.lifecycleOwner = this

        linkViewModel(binding, _viewModel)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Timber.tag(LogTags.NAVIGATION).d("${javaClass.simpleName} fragment is active")
    }

    override fun onDestroyView() {
        coroutineContext.cancelChildren()
        super.onDestroyView()
    }

    override fun onDestroy() {
        releaseInjection()
        super.onDestroy()
    }

    abstract fun provideViewModelType(): Class<VM>

    @LayoutRes
    protected abstract fun layoutResId(): Int

    protected abstract fun inject()

    protected abstract fun releaseInjection()

    protected abstract fun linkViewModel(binding: VDB, viewModel: VM)

    protected open fun processViewCommand(command: ViewCommand) {}

    protected fun getDashboardFragment(fragment : Fragment?) : DashboardFragment? =
        when (fragment) {
            null -> null
            is DashboardFragment -> fragment
            else -> getDashboardFragment(fragment.parentFragment)
        }

    /**
     * Process input _command
     * @return true if the _command has been processed
     */
    private fun processViewCommandGeneral(command: ViewCommand): Boolean =
        when(command) {
            is ShowMessageCommand -> {
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
                loadingDialog?.show(requireFragmentManager(), LoadingDialog::class.java.name)
            }
        } else {
            if(loadingDialog != null){
                loadingDialog?.dismiss()
                loadingDialog = null
            }
        }
    }
}