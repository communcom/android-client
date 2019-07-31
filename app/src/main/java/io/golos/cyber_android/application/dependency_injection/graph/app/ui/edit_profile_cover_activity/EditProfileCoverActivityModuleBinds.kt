package io.golos.cyber_android.application.dependency_injection.graph.app.ui.edit_profile_cover_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.profile.edit.cover.EditProfileCoverViewModel
import io.golos.domain.dependency_injection.scopes.ActivityScope
import io.golos.domain.interactors.user.UserMetadataUseCase

@Module
abstract class EditProfileCoverActivityModuleBinds {
    @Binds
    abstract fun provideUserMetadataUseCase(useCase: UserMetadataUseCase): UserMetadataUseCase

    @Binds
    @ActivityScope
    abstract fun bindViewModelFactory(factory: ActivityViewModelFactory): ViewModelProvider.Factory

    // EditProfileCoverFragment -> EditProfileCoverActivity
    @Binds
    @IntoMap
    @ViewModelKey(EditProfileCoverViewModel::class)
    abstract fun provideEditProfileCoverViewModel(vieModel: EditProfileCoverViewModel): ViewModel
}