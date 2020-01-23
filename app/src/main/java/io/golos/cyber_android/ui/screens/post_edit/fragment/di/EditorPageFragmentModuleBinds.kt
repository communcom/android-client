package io.golos.cyber_android.ui.screens.post_edit.fragment.di

import dagger.Binds
import dagger.Module
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.screens.post_edit.fragment.model.EditorPageModel
import io.golos.cyber_android.ui.screens.post_edit.fragment.model.EditorPageModelImpl

@Module
abstract class EditorPageFragmentModuleBinds {
    @Binds
    abstract fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    abstract fun bindModel(model: EditorPageModelImpl): EditorPageModel
}