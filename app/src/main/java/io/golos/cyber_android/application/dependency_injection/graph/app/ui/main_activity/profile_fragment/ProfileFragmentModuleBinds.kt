package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.profile_fragment

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.profile.old_profile.OldProfileViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.use_cases.user.UserMetadataUseCase
import io.golos.domain.use_cases.user.UserMetadataUseCaseImpl

@Module
abstract class ProfileFragmentModuleBinds {
    @Binds
    abstract fun provideUserMetadataUseCase(useCase: UserMetadataUseCaseImpl): UserMetadataUseCase

    @Binds
    @FragmentScope
    abstract fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(OldProfileViewModel::class)
    abstract fun provideProfileViewModel(vieModel: OldProfileViewModel): ViewModel
}