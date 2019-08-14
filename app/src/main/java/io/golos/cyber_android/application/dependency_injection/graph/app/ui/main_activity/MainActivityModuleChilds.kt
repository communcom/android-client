package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity

import dagger.Module
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.CommunitiesFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.feed_fragment.MyFeedFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.notifications_fragment.NotificationsFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.profile_fragment.ProfileFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.trending_feed.TrendingFeedFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.user_posts_feed.UserPostsFeedFragmentComponent

@Module(subcomponents = [
    MyFeedFragmentComponent::class,
    NotificationsFragmentComponent::class,
    ProfileFragmentComponent::class,
    TrendingFeedFragmentComponent::class,
    UserPostsFeedFragmentComponent::class,
    CommunitiesFragmentComponent::class
])
class MainActivityModuleChilds