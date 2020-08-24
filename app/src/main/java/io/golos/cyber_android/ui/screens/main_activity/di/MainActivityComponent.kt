package io.golos.cyber_android.ui.screens.main_activity.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.communities_list.di.CommunitiesListFragmentComponent
import io.golos.cyber_android.ui.screens.communities_list.di.CommunitiesListFragmentTabComponent
import io.golos.cyber_android.ui.screens.community_get_points.di.GetCommunityPointsFragmentComponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_communities.di.DiscoveryCommunitiesFragmentComponent
import io.golos.cyber_android.ui.screens.donate_send_points.di.DonateSendPointsFragmentComponent
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import io.golos.cyber_android.ui.screens.wallet.di.WalletFragmentComponent
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Subcomponent(
    modules = [
        MainActivityModuleChilds::class,
        MainActivityModuleBinds::class
    ]
)
@ActivityScope
interface MainActivityComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): MainActivityComponent
    }

    val communitiesFragmentComponent: CommunitiesListFragmentComponent.Builder
    val communitiesFragmentTabComponent: CommunitiesListFragmentTabComponent.Builder
    val communitiesFragmentDiscoveryComponent: DiscoveryCommunitiesFragmentComponent.Builder
    val walletFragmentComponent: WalletFragmentComponent.Builder
    val donateSendPointsFragmentComponent: DonateSendPointsFragmentComponent.Builder
    val getCommunityPointsFragmentComponent: GetCommunityPointsFragmentComponent.Builder

    fun inject(activity: MainActivity)
}