package io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_bio

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.profile_bio.model.ProfileBioModel
import io.golos.cyber_android.ui.screens.profile_bio.model.ProfileBioModelImpl
import io.golos.cyber_android.ui.screens.profile_bio.view_model.ProfileBioViewModel

@Module
abstract class ProfileBioFragmentModuleBinds {
    @Binds
    @IntoMap
    @ViewModelKey(ProfileBioViewModel::class)
    abstract fun provideProfileBioViewModel(viewModel: ProfileBioViewModel): ViewModel

    @Binds
    abstract fun provideProfileBioModel(model: ProfileBioModelImpl): ProfileBioModel
}