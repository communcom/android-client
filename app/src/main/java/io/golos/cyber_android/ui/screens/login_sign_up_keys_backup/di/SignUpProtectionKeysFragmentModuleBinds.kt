package io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.model.SignUpProtectionKeysModel
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.model.SignUpProtectionKeysModelImpl
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.model.page_renderer.PageRenderer
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.model.page_renderer.PageRendererImpl
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.view_model.SignUpProtectionKeysViewModel

@Module
abstract class SignUpProtectionKeysFragmentModuleBinds {
    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(SignUpProtectionKeysViewModel::class)
    abstract fun provideViewModel(viewModel: SignUpProtectionKeysViewModel): ViewModel

    @Binds
    abstract fun provideModel(model: SignUpProtectionKeysModelImpl): SignUpProtectionKeysModel

    @Binds
    abstract fun providePageRenderer(renderer: PageRendererImpl): PageRenderer
}