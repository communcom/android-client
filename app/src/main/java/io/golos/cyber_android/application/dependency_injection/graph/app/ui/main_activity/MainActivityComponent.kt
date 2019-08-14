package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity

import dagger.Subcomponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.CommunitiesFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.feed_fragment.MyFeedFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.notifications_fragment.NotificationsFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.profile_fragment.ProfileFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.trending_feed.TrendingFeedFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.user_posts_feed.UserPostsFeedFragmentComponent
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Subcomponent(modules = [
    MainActivityModuleChilds::class,
    MainActivityModuleBinds::class
])
@ActivityScope
interface MainActivityComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): MainActivityComponent
    }

    val myFeedFragmentComponent: MyFeedFragmentComponent.Builder
    val notificationsFragmentComponent: NotificationsFragmentComponent.Builder
    val profileFragmentComponent: ProfileFragmentComponent.Builder
    val trendingFeedFragmentComponent: TrendingFeedFragmentComponent.Builder
    val userPostsFeedFragmentComponent: UserPostsFeedFragmentComponent.Builder
    val communitiesFragmentComponent: CommunitiesFragmentComponent.Builder

    fun inject(activity: MainActivity)
}