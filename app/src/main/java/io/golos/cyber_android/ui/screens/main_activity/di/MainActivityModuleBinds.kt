package io.golos.cyber_android.ui.screens.main_activity.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.main_activity.model.MainModel
import io.golos.cyber_android.ui.screens.main_activity.model.MainModelImpl
import io.golos.cyber_android.ui.screens.main_activity.view_model.MainViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ActivityViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Module
abstract class MainActivityModuleBinds {
    @Binds
    @ActivityScope
    abstract fun bindViewModelFactory(factory: ActivityViewModelFactoryImpl): ActivityViewModelFactory

    @Binds
    @ActivityScope
    abstract fun bindModel(model: MainModelImpl): MainModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun provideAuthViewModel(model: MainViewModel): ViewModel

}