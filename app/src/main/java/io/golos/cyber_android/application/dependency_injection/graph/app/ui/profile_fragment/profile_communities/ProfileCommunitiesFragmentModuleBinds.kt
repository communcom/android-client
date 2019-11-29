package io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_communities

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.profile_communities.model.ProfileCommunitiesModel
import io.golos.cyber_android.ui.screens.profile_communities.model.ProfileCommunitiesModelImpl
import io.golos.cyber_android.ui.screens.profile_communities.view_model.ProfileCommunitiesViewModel

@Module
abstract class ProfileCommunitiesFragmentModuleBinds {
    @Binds
    @IntoMap
    @ViewModelKey(ProfileCommunitiesViewModel::class)
    abstract fun provideProfileBioViewModel(viewModel: ProfileCommunitiesViewModel): ViewModel

    @Binds
    abstract fun provideProfileCommunitiesModel(model: ProfileCommunitiesModelImpl): ProfileCommunitiesModel
}