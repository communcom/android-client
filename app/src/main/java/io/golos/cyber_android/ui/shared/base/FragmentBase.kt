package io.golos.cyber_android.ui.shared.base

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import io.golos.cyber_android.ui.dialogs.LoadingDialog
import io.golos.cyber_android.ui.shared.extensions.getMessage
import io.golos.cyber_android.ui.shared.helper.UIHelper
import io.golos.cyber_android.ui.shared.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.utils.id.IdUtil
import javax.inject.Inject

abstract class FragmentBase : Fragment() {
    companion object {
        private const val INJECTION_KEY = "INJECTION_KEY"
    }

    private lateinit var injectionKey: String

    private val loadingDialog = LoadingDialog()

    private var wasAdded = false

    protected open val isBackHandlerEnabled = false

    @Inject
    protected lateinit var uiHelper: UIHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectionKey = savedInstanceState?.getString(INJECTION_KEY) ?: IdUtil.generateStringId()
        inject(injectionKey)

        if (isBackHandlerEnabled) {
            requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(isBackHandlerEnabled) {
                override fun handleOnBackPressed() {
                    onBackPressed()
                }
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(INJECTION_KEY, injectionKey)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isRemoving) {
            releaseInjection(injectionKey)
        }
    }

    protected open fun inject(key: String) {}

    protected open fun releaseInjection(key: String) {}

    /**
     * Process input _command
     * @return true if the _command has been processed
     */
    protected fun processViewCommandGeneral(command: ViewCommand) = when (command) {
        is ShowMessageResCommand -> uiHelper.showMessage(command.textResId, command.isError)
        is ShowMessageTextCommand -> uiHelper.showMessage(command.text, command.isError)
        is SetLoadingVisibilityCommand -> setLoadingVisibility(command.isVisible)
        else -> processViewCommand(command)
    }

    protected open fun processViewCommand(command: ViewCommand) {}

    protected fun setLoadingVisibility(isVisible: Boolean) = if (isVisible) {
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
            try {
                loadingDialog.dismiss()
                wasAdded = false
            } catch (e: Exception) {
                uiHelper.showMessage(e.getMessage(requireContext()))
            }
        }
    }

    protected open fun onBackPressed() {}
}