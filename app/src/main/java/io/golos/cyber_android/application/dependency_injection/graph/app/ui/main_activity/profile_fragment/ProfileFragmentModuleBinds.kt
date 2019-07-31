package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.profile_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.profile.ProfileViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.interactors.user.UserMetadataUseCase

@Module
abstract class ProfileFragmentModuleBinds {
    @Binds
    abstract fun provideUserMetadataUseCase(useCase: UserMetadataUseCase): UserMetadataUseCase

    @Binds
    @FragmentScope
    abstract fun bindViewModelFactory(factory: FragmentViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun provideProfileViewModel(vieModel: ProfileViewModel): ViewModel
}