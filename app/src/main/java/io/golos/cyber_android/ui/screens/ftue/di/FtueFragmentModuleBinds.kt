package io.golos.cyber_android.ui.screens.ftue.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.ftue.model.FtueModel
import io.golos.cyber_android.ui.screens.ftue.model.FtueModelImpl
import io.golos.cyber_android.ui.screens.ftue.view_model.FtueViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
interface FtueFragmentModuleBinds {

    @Binds
    @ViewModelKey(FtueViewModel::class)
    @IntoMap
    fun bindViewModel(viewModel: FtueViewModel): ViewModel

    @Binds
    @FragmentScope
    fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @FragmentScope
    fun bindModel(model: FtueModelImpl): FtueModel

}