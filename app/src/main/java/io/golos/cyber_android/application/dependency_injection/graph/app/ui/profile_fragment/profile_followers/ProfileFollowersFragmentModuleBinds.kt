package io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_followers

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.profile_followers.model.ProfileFollowersModel
import io.golos.cyber_android.ui.screens.profile_followers.model.ProfileFollowersModelImpl
import io.golos.cyber_android.ui.screens.profile_followers.view_model.ProfileFollowersViewModel

@Module
abstract class ProfileFollowersFragmentModuleBinds {
    @Binds
    @IntoMap
    @ViewModelKey(ProfileFollowersViewModel::class)
    abstract fun provideProfileFollowersViewModel(viewModel: ProfileFollowersViewModel): ViewModel

    @Binds
    abstract fun provideProfileFollowersModel(model: ProfileFollowersModelImpl): ProfileFollowersModel
}