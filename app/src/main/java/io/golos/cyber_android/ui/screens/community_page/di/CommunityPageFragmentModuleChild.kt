package io.golos.cyber_android.ui.screens.community_page.di

import dagger.Module
import io.golos.cyber_android.ui.screens.community_page_leaders_list.di.CommunityPageLeadsListComponent
import io.golos.cyber_android.ui.screens.community_page_members.di.CommunityPageMembersComponent

@Module(subcomponents = [
    CommunityPageLeadsListComponent::class,
    CommunityPageMembersComponent::class
])
class CommunityPageFragmentModuleChild