package io.golos.cyber_android.application.dependency_injection.graph.app.ui.editor_page_fragment

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
abstract class EditorPageFragmentModuleBinds {
    @Binds
    @FragmentScope
    abstract fun bindViewModelFactory(factory: FragmentViewModelFactory): ViewModelProvider.Factory
}