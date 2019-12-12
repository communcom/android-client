package io.golos.cyber_android.ui.screens.main_activity.di

import dagger.Module
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_list_fragment.CommunitiesListFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_list_fragment.CommunitiesListFragmentTabComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.notifications_fragment.NotificationsFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.profile_fragment.OldProfileFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.trending_feed.TrendingFeedFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.user_posts_feed.UserPostsFeedFragmentComponent
import io.golos.cyber_android.ui.screens.my_feed.di.MyFeedFragmentComponent

@Module(subcomponents = [
    MyFeedFragmentComponent::class,
    NotificationsFragmentComponent::class,
    OldProfileFragmentComponent::class,
    TrendingFeedFragmentComponent::class,
    UserPostsFeedFragmentComponent::class,
    CommunitiesListFragmentComponent::class,
    CommunitiesListFragmentTabComponent::class
])
class MainActivityModuleChilds