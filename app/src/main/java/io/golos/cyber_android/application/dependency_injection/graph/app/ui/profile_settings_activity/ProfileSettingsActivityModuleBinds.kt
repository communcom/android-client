package io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_settings_activity

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.profile.old_profile.edit.settings.ProfileSettingsViewModel

@Module
abstract class ProfileSettingsActivityModuleBinds {
    @Binds
    abstract fun bindViewModelFactory(factory: ActivityViewModelFactoryImpl): ActivityViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(ProfileSettingsViewModel::class)
    abstract fun provideProfileSettingsViewModel(viewModel: ProfileSettingsViewModel): ViewModel
}