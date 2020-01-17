package io.golos.cyber_android.ui.screens.community_page_members.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.community_page_members.model.CommunityPageMembersModel
import io.golos.cyber_android.ui.screens.community_page_members.model.CommunityPageMembersModelImpl
import io.golos.cyber_android.ui.screens.community_page_members.model.list_worker.ListWorkerCommunityMembers
import io.golos.cyber_android.ui.screens.community_page_members.view_model.CommunityPageMembersViewModel
import io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers.UsersListWorker
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class CommunityPageMembersModuleBinds {
    @Binds
    @IntoMap
    @ViewModelKey(CommunityPageMembersViewModel::class)
    abstract fun provideCommunityPageMembersViewModel(viewModel: CommunityPageMembersViewModel): ViewModel

    @Binds
    abstract fun provideCommunityPageMembersModel(model: CommunityPageMembersModelImpl): CommunityPageMembersModel

    @Binds
    abstract fun provideListWorkerCommunityMember(worker: ListWorkerCommunityMembers): UsersListWorker
}