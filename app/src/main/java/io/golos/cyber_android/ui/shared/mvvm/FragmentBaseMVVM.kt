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
import io.golos.cyber_android.ui.screens.dashboard.view.DashboardFragment
import io.golos.cyber_android.ui.shared.base.FragmentBase
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
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
abstract class FragmentBaseMVVM<VDB: ViewDataBinding, VM: ViewModelBase<out ModelBase>> : FragmentBase(), CoroutineScope {
    private lateinit var binding: VDB

    private lateinit var _viewModel: VM

    protected val viewModel: VM
        get() = _viewModel

    @Inject
    internal lateinit var viewModelFactory: FragmentViewModelFactory

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main + errorHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProviders.of(this, viewModelFactory)[provideViewModelType()]
        _viewModel = viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _viewModel.command.observe({viewLifecycleOwner.lifecycle}) { processViewCommandGeneral(it) }

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

    abstract fun provideViewModelType(): Class<VM>

    @LayoutRes
    protected abstract fun layoutResId(): Int

    protected abstract fun linkViewModel(binding: VDB, viewModel: VM)

    protected fun getDashboardFragment(fragment : Fragment?) : DashboardFragment? =
        when (fragment) {
            null -> null
            is DashboardFragment -> fragment
            else -> getDashboardFragment(fragment.parentFragment)
        }

    protected fun setSelectAction(resultCode: Int, putArgsAction: Intent.() -> Unit = {}) {
        targetFragment?.onActivityResult(targetRequestCode, resultCode, Intent().also { intent ->
            putArgsAction.invoke(intent)
        })
    }
}