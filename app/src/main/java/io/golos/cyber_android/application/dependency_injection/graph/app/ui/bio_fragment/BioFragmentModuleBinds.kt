package io.golos.cyber_android.application.dependency_injection.graph.app.ui.bio_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.ViewModelKey
import io.golos.cyber_android.ui.shared_fragments.bio.EditProfileBioViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.interactors.user.UserMetadataUseCase

@Module
abstract class BioFragmentModuleBinds {
    @Binds
    @IntoMap
    @ViewModelKey(EditProfileBioViewModel::class)
    abstract fun provideEditProfileBioViewModel(viewModel: EditProfileBioViewModel): ViewModel

    @Binds
    abstract fun provideUserMetadataUseCase(useCase: UserMetadataUseCase): UserMetadataUseCase

    @Binds
    @FragmentScope
    abstract fun bindViewModelFactory(factory: FragmentViewModelFactory): ViewModelProvider.Factory
}