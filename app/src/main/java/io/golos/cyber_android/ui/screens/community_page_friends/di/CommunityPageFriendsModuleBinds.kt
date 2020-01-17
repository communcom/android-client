package io.golos.cyber_android.ui.screens.community_page_friends.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.community_page_friends.model.CommunityPageFriendsModel
import io.golos.cyber_android.ui.screens.community_page_friends.model.CommunityPageFriendsModelImpl
import io.golos.cyber_android.ui.screens.community_page_friends.view_model.CommunityPageFriendsViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class CommunityPageFriendsModuleBinds {
    @Binds
    @IntoMap
    @ViewModelKey(CommunityPageFriendsViewModel::class)
    abstract fun provideCommunityPageFriendsViewModel(viewModel: CommunityPageFriendsViewModel): ViewModel

    @Binds
    abstract fun provideCommunityPageFriendsModel(model: CommunityPageFriendsModelImpl): CommunityPageFriendsModel
}