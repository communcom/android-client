package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment

import dagger.Module
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.discover_fragment.DiscoverFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.my_community_fragment.MyCommunityFragmentComponent

@Module(subcomponents = [
    DiscoverFragmentComponent::class,
    MyCommunityFragmentComponent::class
])
class CommunitiesFragmentModuleChilds