package io.golos.cyber_android.ui.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.login_sign_up_bio.di.BioFragmentComponent
import io.golos.cyber_android.ui.screens.community_page.di.CommunityPageFragmentComponent
import io.golos.cyber_android.ui.screens.community_page_about.di.CommunityPageAboutFragmentComponent
import io.golos.cyber_android.ui.screens.community_page_rules.di.CommunityPageRulesFragmentComponent
import io.golos.cyber_android.ui.dialogs.select_community_dialog.di.SelectCommunityDialogComponent
import io.golos.cyber_android.ui.screens.post_edit.di.EditorPageFragmentComponent
import io.golos.cyber_android.ui.screens.feed.di.FeedFragmentComponent
import io.golos.cyber_android.ui.screens.feedback_activity.di.FeedbackActivityComponent
import io.golos.cyber_android.ui.screens.in_app_auth_activity.di.InAppAuthActivityComponent
import io.golos.cyber_android.ui.screens.post_filters.di.PostFiltersFragmentComponent
import io.golos.cyber_android.ui.screens.post_view.di.PostPageFragmentComponent
import io.golos.cyber_android.ui.screens.profile.di.ProfileFragmentComponent
import io.golos.cyber_android.ui.screens.subscriptions.di.SubscriptionsFragmentComponent
import io.golos.cyber_android.ui.shared.widgets.CommentWidgetEdit
import io.golos.cyber_android.ui.shared.widgets.CommentWidgetNew
import io.golos.cyber_android.ui.shared.widgets.pin.PinDigit
import io.golos.cyber_android.ui.screens.community_page_post.di.CommunityPostFragmentComponent
import io.golos.cyber_android.ui.screens.dashboard.di.DashboardFragmentComponent
import io.golos.cyber_android.ui.screens.ftue.di.FtueFragmentComponent
import io.golos.cyber_android.ui.screens.ftue_finish.di.FtueFinishFragmentComponent
import io.golos.cyber_android.ui.screens.ftue_search_community.di.FtueSearchCommunityFragmentComponent
import io.golos.cyber_android.ui.screens.login_activity.di.LoginActivityComponent
import io.golos.cyber_android.ui.screens.main_activity.di.MainActivityComponent
import io.golos.cyber_android.ui.screens.feed_my.di.MyFeedFragmentComponent
import io.golos.cyber_android.ui.screens.post_report.di.PostReportFragmentComponent
import io.golos.cyber_android.ui.screens.profile.di.ProfileExternalUserFragmentComponent
import io.golos.cyber_android.ui.screens.profile_comments.di.ProfileCommentsFragmentComponent
import io.golos.domain.dependency_injection.scopes.UIScope

@Subcomponent(modules = [
    UIModule::class,
    UIModuleBinds::class,
    UIModuleChilds::class
])
@UIScope
interface UIComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): UIComponent
    }

    val bioFragment: BioFragmentComponent.Builder
    val editorPageFragment: EditorPageFragmentComponent.Builder
    val loginActivity: LoginActivityComponent.Builder
    val mainActivity: MainActivityComponent.Builder
    val postPageFragment: PostPageFragmentComponent.Builder
    val inAppAuthActivity: InAppAuthActivityComponent.Builder
    val feedbackActivity: FeedbackActivityComponent.Builder
    val selectCommunityDialog: SelectCommunityDialogComponent.Builder
    val subscriptionsFragment: SubscriptionsFragmentComponent.Builder
    val communityPageFragment: CommunityPageFragmentComponent.Builder
    val communityPageAboutFragment: CommunityPageAboutFragmentComponent.Builder
    val communityPageRulesFragment: CommunityPageRulesFragmentComponent.Builder
    val feedFragment: FeedFragmentComponent.Builder
    val postFiltersFragment: PostFiltersFragmentComponent.Builder
    val postReportFragment: PostReportFragmentComponent.Builder
    val postsListFragment: MyFeedFragmentComponent.Builder
    val communityPostFragment: CommunityPostFragmentComponent.Builder
    val profileFragment: ProfileFragmentComponent.Builder
    val ftueFragment: FtueFragmentComponent.Builder
    val ftueSearchCommunityFragment: FtueSearchCommunityFragmentComponent.Builder
    val ftueFinishFragmentComponent: FtueFinishFragmentComponent.Builder
    val dashboardFragmentComponent: DashboardFragmentComponent.Builder
    val profileExternalUserFragment: ProfileExternalUserFragmentComponent.Builder
    val profileCommentsFragmentComponent: ProfileCommentsFragmentComponent.Builder

    fun inject(pinDigit: PinDigit)
    fun inject(widget: CommentWidgetNew)
    fun inject(widget: CommentWidgetEdit)
}