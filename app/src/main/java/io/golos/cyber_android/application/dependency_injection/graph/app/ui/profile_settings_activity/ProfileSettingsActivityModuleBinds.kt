package io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_settings_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.ActivityViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.ViewModelKey
import io.golos.cyber_android.ui.screens.profile.edit.settings.ProfileSettingsViewModel
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Module
abstract class ProfileSettingsActivityModuleBinds {
    @Binds
    @ActivityScope
    abstract fun bindViewModelFactory(factory: ActivityViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ProfileSettingsViewModel::class)
    abstract fun provideProfileSettingsViewModel(viewModel: ProfileSettingsViewModel): ViewModel
}