package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment

import dagger.Binds
import dagger.Module
import io.golos.cyber_android.ui.common.formatters.size.FollowersSizeFormatter
import io.golos.cyber_android.ui.common.formatters.size.SizeFormatter
import io.golos.cyber_android.ui.screens.main_activity.communities.search_bridge.SearchBridge
import io.golos.cyber_android.ui.screens.main_activity.communities.search_bridge.SearchBridgeChild
import io.golos.cyber_android.ui.screens.main_activity.communities.search_bridge.SearchBridgeParent
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
abstract class CommunitiesFragmentModuleBinds {
    @Binds
    @FragmentScope
    abstract fun bindSearchBridgeParent(bridge: SearchBridge): SearchBridgeParent

    @Binds
    @FragmentScope
    abstract fun bindSearchBridgeChild(bridge: SearchBridge): SearchBridgeChild

    @Binds
    @FragmentScope
    abstract fun provideFollowersSizeFormatter(formatter: FollowersSizeFormatter): SizeFormatter
}