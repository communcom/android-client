package io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.FingerprintAuthViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
abstract class FingerprintAuthFragmentModuleBinds {
    @Binds
    @IntoMap
    @ViewModelKey(FingerprintAuthViewModel::class)
    abstract fun provideFingerprintAuthViewModel(viewModel: FingerprintAuthViewModel): ViewModel

    @Binds
    @FragmentScope
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory
}