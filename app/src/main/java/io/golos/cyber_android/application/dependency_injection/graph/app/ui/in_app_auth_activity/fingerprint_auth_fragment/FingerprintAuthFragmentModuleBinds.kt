package io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.fingerprint_auth_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.model.FingerprintAuthModel
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.model.FingerprintAuthModelImpl
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
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactory): ViewModelProvider.Factory
}