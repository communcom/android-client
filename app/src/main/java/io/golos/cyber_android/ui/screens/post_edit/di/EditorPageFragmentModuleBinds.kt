package io.golos.cyber_android.ui.screens.post_edit.di

import dagger.Binds
import dagger.Module
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.screens.post_edit.model.EditorPageModel
import io.golos.cyber_android.ui.screens.post_edit.model.EditorPageModelImpl

@Module
abstract class EditorPageFragmentModuleBinds {
    @Binds
    abstract fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    abstract fun bindModel(model: EditorPageModelImpl): EditorPageModel
}