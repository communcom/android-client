package io.golos.cyber_android.ui.screens.main_activity.di

import dagger.Module
import io.golos.cyber_android.ui.screens.communities_list.di.CommunitiesListFragmentComponent
import io.golos.cyber_android.ui.screens.communities_list.di.CommunitiesListFragmentTabComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.notifications_fragment.NotificationsFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.trending_feed.TrendingFeedFragmentComponent
import io.golos.cyber_android.ui.screens.my_feed.di.MyFeedFragmentComponent

@Module(subcomponents = [
    MyFeedFragmentComponent::class,
    NotificationsFragmentComponent::class,
    TrendingFeedFragmentComponent::class,
    CommunitiesListFragmentComponent::class,
    CommunitiesListFragmentTabComponent::class
])
class MainActivityModuleChilds