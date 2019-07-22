package io.golos.cyber_android.ui.common.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.golos.domain.dependency_injection.scopes.ActivityScope
import io.golos.domain.dependency_injection.scopes.FragmentScope
import javax.inject.Inject
import javax.inject.Provider

@Suppress("UNCHECKED_CAST")
open class ViewModelFactory
@Inject
constructor(
    private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = viewModels[modelClass]?.get() as T
}

@FragmentScope
class FragmentViewModelFactory(viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>): ViewModelFactory(viewModels)

@ActivityScope
class ActivityViewModelFactory(viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>): ViewModelFactory(viewModels)
