package io.golos.cyber_android.ui.common.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.common.helper.UIHelper
import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.dialogs.LoadingDialog
import io.golos.domain.AppResourcesProvider
import io.golos.domain.LogTags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Base class for all fragments
 */
abstract class FragmentBaseMVVM<TB: ViewDataBinding, TM: ModelBase, TVM: ViewModelBase<TM>> : Fragment(), CoroutineScope {

    private lateinit var binding: TB

    private lateinit var _viewModel: TVM
    protected val viewModel: TVM
        get() = _viewModel

    protected var activeDialog: AlertDialog? = null

    private val loadingDialog = LoadingDialog()
    private var wasAdded = false

    @Inject
    internal lateinit var resourcesProvider: AppResourcesProvider

    @Inject
    internal lateinit var uiHelper: UIHelper

    @Inject
    internal lateinit var viewModelFactory: FragmentViewModelFactory

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inject()

        _viewModel = ViewModelProviders.of(this, viewModelFactory)[provideViewModelType()]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _viewModel.command.observe({viewLifecycleOwner.lifecycle}) {
            if(!processViewCommandGeneral(it)) {
                processViewCommand(it)
            }
        }

        binding = DataBindingUtil.inflate(inflater, provideLayout(), container, false)
        binding.lifecycleOwner = this

        linkViewModel(binding, _viewModel)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        App.logger.log(LogTags.NAVIGATION, "${javaClass.simpleName} fragment is active")
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Close a dialog to avoid leak of view
        activeDialog?.takeIf { it.isShowing }?.dismiss()
        activeDialog = null
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseInjection()
    }

    abstract fun provideViewModelType(): Class<TVM>

    @LayoutRes
    protected abstract fun provideLayout(): Int

    protected abstract fun inject()

    protected open fun releaseInjection() {}

    protected abstract fun linkViewModel(binding: TB, viewModel: TVM)

    protected open fun processViewCommand(command: ViewCommand) {}

    /**
     * Process input command
     * @return true if the command has been processed
     */
    private fun processViewCommandGeneral(command: ViewCommand): Boolean {
        when(command) {
            is ShowMessageCommand -> uiHelper.showMessage(command.textResId)
            is SetLoadingVisibilityCommand -> setLoadingVisibility(command.isVisible)
        }
        return false
    }

    private fun setLoadingVisibility(isVisible: Boolean) {
        if (isVisible) {
            if (loadingDialog.dialog?.isShowing != true && !loadingDialog.isAdded && !wasAdded) {
                loadingDialog.show(requireFragmentManager(), "loading")
                wasAdded = true
            }
        } else {
            if (loadingDialog.fragmentManager != null && wasAdded) {
                loadingDialog.dismiss()
                wasAdded = false
            }
        }
    }
}