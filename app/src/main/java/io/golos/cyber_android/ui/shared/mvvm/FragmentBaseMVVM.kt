package io.golos.cyber_android.ui.shared.mvvm

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import io.golos.cyber_android.ui.dialogs.LoadingDialog
import io.golos.cyber_android.ui.screens.dashboard.view.DashboardFragment
import io.golos.cyber_android.ui.shared.helper.UIHelper
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.LogTags
import io.golos.domain.utils.IdUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Base class for all fragments
 */
abstract class FragmentBaseMVVM<VDB: ViewDataBinding, VM: ViewModelBase<out ModelBase>> : Fragment(), CoroutineScope {
    companion object {
        private const val INJECTION_KEY = "INJECTION_KEY"
    }

    private lateinit var injectionKey: String

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

        injectionKey = savedInstanceState?.getString(INJECTION_KEY) ?: IdUtil.generateStringId()
        inject(injectionKey)

        val viewModel = ViewModelProviders.of(this, viewModelFactory)[provideViewModelType()]
        _viewModel = viewModel
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(INJECTION_KEY, injectionKey)
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
        super.onDestroy()

        if(isRemoving) {
            releaseInjection(injectionKey)
        }
    }

    abstract fun provideViewModelType(): Class<VM>

    @LayoutRes
    protected abstract fun layoutResId(): Int

    protected abstract fun inject(key: String)

    protected abstract fun releaseInjection(key: String)

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
            is ShowMessageResCommand -> {
                uiHelper.showMessage(command.textResId)
                true
            }
            is ShowMessageTextCommand -> {
                uiHelper.showMessage(command.text)
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

    protected fun setSelectAction(resultCode: Int, putArgsAction: Intent.() -> Unit = {}) {
        targetFragment?.onActivityResult(targetRequestCode, resultCode, Intent().also { intent ->
            putArgsAction.invoke(intent)
        })
    }
}