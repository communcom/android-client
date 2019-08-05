package io.golos.cyber_android.application.dependency_injection.graph.app.ui.editor_page_fragment

import dagger.Binds
import dagger.Module
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl

@Module
abstract class EditorPageFragmentModuleBinds {
    @Binds
    abstract fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory
}