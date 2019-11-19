package io.golos.cyber_android.application.dependency_injection.graph.app.ui.edit_profile_cover_activity

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.profile.old_profile.edit.cover.EditProfileCoverViewModel
import io.golos.domain.dependency_injection.scopes.ActivityScope
import io.golos.domain.use_cases.user.UserMetadataUseCase
import io.golos.domain.use_cases.user.UserMetadataUseCaseImpl

@Module
abstract class EditProfileCoverActivityModuleBinds {
    @Binds
    abstract fun provideUserMetadataUseCase(useCase: UserMetadataUseCaseImpl): UserMetadataUseCase

    @Binds
    @ActivityScope
    abstract fun bindViewModelFactory(factory: ActivityViewModelFactoryImpl): ActivityViewModelFactory

    // EditProfileCoverFragment -> EditProfileCoverActivity
    @Binds
    @IntoMap
    @ViewModelKey(EditProfileCoverViewModel::class)
    abstract fun provideEditProfileCoverViewModel(vieModel: EditProfileCoverViewModel): ViewModel
}