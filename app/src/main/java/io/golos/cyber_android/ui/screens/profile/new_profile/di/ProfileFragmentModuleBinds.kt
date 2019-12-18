package io.golos.cyber_android.ui.screens.profile.new_profile.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.profile.new_profile.model.ProfileModel
import io.golos.cyber_android.ui.screens.profile.new_profile.model.ProfileModelImpl
import io.golos.cyber_android.ui.screens.profile.new_profile.model.logout.LogoutUseCase
import io.golos.cyber_android.ui.screens.profile.new_profile.model.logout.LogoutUseCaseImpl
import io.golos.cyber_android.ui.screens.profile.new_profile.view_model.ProfileViewModel

@Module
abstract class ProfileFragmentModuleBinds {
    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun provideProfileViewModel(viewModel: ProfileViewModel): ViewModel

    @Binds
    abstract fun provideProfileModel(model: ProfileModelImpl): ProfileModel

    @Binds
    abstract fun provideLogoutUseCase(useCase: LogoutUseCaseImpl): LogoutUseCase
}