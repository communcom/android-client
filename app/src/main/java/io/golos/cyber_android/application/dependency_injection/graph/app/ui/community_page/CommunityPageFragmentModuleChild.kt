package io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page

import dagger.Module
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page.leads_list_fragment.CommunityPageLeadsListComponent

@Module(subcomponents = [
    CommunityPageLeadsListComponent::class
])
class CommunityPageFragmentModuleChild {
}