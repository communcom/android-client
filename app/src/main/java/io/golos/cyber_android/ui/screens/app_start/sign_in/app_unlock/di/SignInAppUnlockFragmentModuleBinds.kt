package io.golos.cyber_android.ui.screens.app_start.sign_in.app_unlock.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.app_start.sign_in.app_unlock.model.AppUnlockModel
import io.golos.cyber_android.ui.screens.app_start.sign_in.app_unlock.model.SignInAppUnlockModelImpl
import io.golos.cyber_android.ui.screens.app_start.sign_in.app_unlock.view_model.SignInAppUnlockViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class SignInAppUnlockFragmentModuleBinds {
    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(SignInAppUnlockViewModel::class)
    abstract fun provideViewModel(viewModel: SignInAppUnlockViewModel): ViewModel

    @Binds
    abstract fun provideModel(model: SignInAppUnlockModelImpl): AppUnlockModel
}