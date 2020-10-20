package io.golos.cyber_android.ui.screens.profile.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.profile.model.ProfileModel
import io.golos.cyber_android.ui.screens.profile.model.ProfileModelExternalUserImpl
import io.golos.cyber_android.ui.screens.profile.model.logout.LogoutUseCase
import io.golos.cyber_android.ui.screens.profile.model.logout.LogoutUseCaseExternalUserImpl
import io.golos.cyber_android.ui.screens.profile.model.notifications_settings.NotificationsSettingsFacade
import io.golos.cyber_android.ui.screens.profile.model.notifications_settings.NotificationsSettingsFacadeExternalUserImpl
import io.golos.cyber_android.ui.screens.profile.view_model.ProfileViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class ProfileFragmentExternalUserModuleBinds {
    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun provideProfileViewModel(viewModel: ProfileViewModel): ViewModel

    @Binds
    abstract fun provideProfileModel(model: ProfileModelExternalUserImpl): ProfileModel

    @Binds
    abstract fun provideLogoutUseCase(useCase: LogoutUseCaseExternalUserImpl): LogoutUseCase

    @Binds
    abstract fun provideNotificationsSettingsFacade(facade: NotificationsSettingsFacadeExternalUserImpl): NotificationsSettingsFacade
}