package io.golos.cyber_android.application.dependency_injection.profile

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.ViewModelKey
import io.golos.cyber_android.ui.screens.profile.ProfileViewModel
import io.golos.cyber_android.ui.screens.profile.edit.avatar.EditProfileAvatarViewModel
import io.golos.cyber_android.ui.screens.profile.edit.bio.EditProfileBioViewModel
import io.golos.cyber_android.ui.screens.profile.edit.cover.EditProfileCoverViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.interactors.user.UserMetadataUseCase

@Module
abstract class ProfileModuleBinds {
    @Binds
    abstract fun provideUserMetadataUseCase(useCase: UserMetadataUseCase): UserMetadataUseCase

    @Binds
    @FragmentScope
    abstract fun bindViewModelFactory(factory: FragmentViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun provideProfileViewModel(vieModel: ProfileViewModel): ProfileViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditProfileAvatarViewModel::class)
    abstract fun provideEditProfileAvatarViewModel(vieModel: EditProfileAvatarViewModel): EditProfileAvatarViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditProfileBioViewModel::class)
    abstract fun provideEditProfileBioViewModel(vieModel: EditProfileBioViewModel): EditProfileBioViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditProfileCoverViewModel::class)
    abstract fun provideEditProfileCoverViewModel(vieModel: EditProfileCoverViewModel): EditProfileCoverViewModel
}