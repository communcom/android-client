package io.golos.cyber_android.application.dependency_injection

import android.content.Context
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.cyber_android.application.dependency_injection.graph.app.AppComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.AppModule
import io.golos.cyber_android.application.dependency_injection.graph.app.DaggerAppComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.UIComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.bio_fragment.BioFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.bio_fragment.BioFragmentModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page.CommunityPageFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page.leads_list_fragment.CommunityPageLeadsListComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page.leads_list_fragment.CommunityPageLeadsListModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page_about.CommunityPageAboutFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page_about.CommunityPageAboutFragmentModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page_rules.CommunityPageRulesFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page_rules.CommunityPageRulesFragmentModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.dialogs.select_community_dialog.SelectCommunityDialogComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.editor_page_fragment.EditorPageFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.editor_page_fragment.EditorPageFragmentModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.feed.FeedFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.feedback_activity.FeedbackActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.followers.FollowersFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.InAppAuthActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.fingerprint_auth_fragment.FingerprintAuthFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.fingerprint_auth_fragment.FingerprintAuthFragmentModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.pin_code_auth_fragment.PinCodeAuthFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.pin_code_auth_fragment.PinCodeAuthFragmentModule
import io.golos.cyber_android.ui.screens.login_activity.di.LoginActivityComponent
import io.golos.cyber_android.ui.screens.login_activity.di.on_boarding.OnBoardingFragmentComponent
import io.golos.cyber_android.ui.screens.login_activity.di.on_boarding.OnBoardingFragmentModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.notifications_fragment.NotificationsFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.trending_feed.TrendingFeedFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.trending_feed.TrendingFeedFragmentModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_filters.PostFiltersFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_bio.ProfileBioFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_bio.ProfileBioFragmentModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_photos.ProfilePhotosFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_photos.ProfilePhotosFragmentModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.subscriptions.SubscriptionsFragmentComponent
import io.golos.cyber_android.ui.dto.*
import io.golos.cyber_android.ui.screens.communities_list.di.CommunitiesListFragmentComponent
import io.golos.cyber_android.ui.screens.communities_list.di.CommunitiesListFragmentModule
import io.golos.cyber_android.ui.screens.communities_list.di.CommunitiesListFragmentTabComponent
import io.golos.cyber_android.ui.screens.dashboard.di.DashboardFragmentComponent
import io.golos.cyber_android.ui.screens.ftue.di.FtueFragmentComponent
import io.golos.cyber_android.ui.screens.ftue_finish.di.FtueFinishFragmentComponent
import io.golos.cyber_android.ui.screens.ftue_search_community.di.FtueSearchCommunityFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_in_qr_code.di.SignInQrCodeFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_in_username.di.SignInUserNameFragmentComponent
import io.golos.cyber_android.ui.screens.main_activity.di.MainActivityComponent
import io.golos.cyber_android.ui.screens.my_feed.di.MyFeedFragmentComponent
import io.golos.cyber_android.ui.screens.post_report.di.PostReportFragmentComponent
import io.golos.cyber_android.ui.screens.post_report.di.PostReportModule
import io.golos.cyber_android.ui.screens.profile.new_profile.di.ProfileExternalUserFragmentComponent
import io.golos.cyber_android.ui.screens.profile.new_profile.di.ProfileFragmentComponent
import io.golos.cyber_android.ui.screens.profile.new_profile.di.ProfileFragmentModule
import io.golos.cyber_android.ui.screens.profile_black_list.di.ProfileBlackListFragmentComponent
import io.golos.cyber_android.ui.screens.profile_black_list.di.ProfileBlackListFragmentModule
import io.golos.cyber_android.ui.screens.profile_comments.di.ProfileCommentsFragmentComponent
import io.golos.cyber_android.ui.screens.profile_comments.di.ProfileCommentsModule
import io.golos.cyber_android.ui.screens.profile_communities.di.ProfileCommunitiesExternalUserFragmentComponent
import io.golos.cyber_android.ui.screens.profile_communities.di.ProfileCommunitiesFragmentComponent
import io.golos.cyber_android.ui.screens.profile_communities.di.ProfileCommunitiesFragmentModule
import io.golos.cyber_android.ui.screens.profile_followers.di.ProfileFollowersExternalUserFragmentComponent
import io.golos.cyber_android.ui.screens.profile_followers.di.ProfileFollowersFragmentComponent
import io.golos.cyber_android.ui.screens.profile_followers.di.ProfileFollowersFragmentModule
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsExternalUserFragmentComponent
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsFragmentComponent
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsFragmentModule
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsLikedFragmentComponent
import io.golos.domain.commun_entities.CommunityId
import io.golos.domain.dto.PostsConfigurationDomain
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.use_cases.model.CommunityModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlin.reflect.KClass

/** Storage for Dagger components on application level  */
class DependencyInjectionStorage(private val appContext: Context) {

    private val components = mutableMapOf<KClass<*>, Any>()

    inline fun <reified T>get(vararg args: Any?): T = getComponent(T::class, args)

    inline fun <reified T>release() = releaseComponent(T::class)

    @Suppress("UNCHECKED_CAST")
    fun <T>getComponent(type: KClass<*>, args: Array<out Any?>): T {
        var result = components[type]
        if(result == null) {
            result = provideComponent<T>(type, args)
            components[type] = result!!
        }
        return result as T
    }

    fun releaseComponent(type: KClass<*>) = components.remove(type)

    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    private fun <T>provideComponent(type: KClass<*>, args: Array<out Any?>): T {
        @Suppress("EXPERIMENTAL_API_USAGE")
        return when(type) {
            AppComponent::class -> DaggerAppComponent.builder().appModule(AppModule(appContext)).build()

            UIComponent::class -> get<AppComponent>().ui.build()

            BioFragmentComponent::class ->
                get<UIComponent>()
                    .bioFragment
                    .init(BioFragmentModule(args[0] as CyberName))
                    .build()

            EditorPageFragmentComponent::class ->
                get<UIComponent>()
                    .editorPageFragment
                    .init(
                        EditorPageFragmentModule(
                            args[0] as ContentId?
                        )
                    )
                    .build()

            ProfileFragmentComponent::class ->
                get<UIComponent>()
                    .profileFragment
                    .init(ProfileFragmentModule(args[0] as UserIdDomain))
                    .build()

            ProfileExternalUserFragmentComponent::class ->
                get<UIComponent>()
                    .profileExternalUserFragment
                    .init(ProfileFragmentModule(args[0] as UserIdDomain))
                    .build()

            ProfilePhotosFragmentComponent::class ->
                get<ProfileFragmentComponent>()
                    .photosFragment
                    .init(ProfilePhotosFragmentModule(args[0] as ProfileItem, args[1] as String?))
                    .build()

            ProfileBioFragmentComponent::class ->
                get<ProfileFragmentComponent>()
                    .bioFragment
                    .init(ProfileBioFragmentModule(args[0] as String?))
                    .build()

            ProfileCommunitiesFragmentComponent::class ->
                get<ProfileFragmentComponent>()
                    .communitiesFragment
                    .init(ProfileCommunitiesFragmentModule(args[0] as ProfileCommunities))
                    .build()

            ProfileCommunitiesExternalUserFragmentComponent::class ->
                get<ProfileExternalUserFragmentComponent>()
                    .communitiesFragment
                    .init(ProfileCommunitiesFragmentModule(args[0] as ProfileCommunities))
                    .build()

            ProfileFollowersFragmentComponent::class ->
                get<ProfileFragmentComponent>()
                    .followersFragment
                    .init(
                        ProfileFollowersFragmentModule(
                            args[0] as FollowersFilter,
                            args[1] as Int,
                            args[2] as List<UserDomain>
                        )
                    )
                    .build()

            ProfileFollowersExternalUserFragmentComponent::class ->
                get<ProfileExternalUserFragmentComponent>()
                    .followersFragment
                    .init(
                        ProfileFollowersFragmentModule(
                            args[0] as FollowersFilter,
                            args[1] as Int,
                            args[2] as List<UserDomain>
                        )
                    )
                    .build()

            ProfilePostsExternalUserFragmentComponent::class ->
                get<ProfileExternalUserFragmentComponent>()
                    .profilePostsFragment
                    .init(ProfilePostsFragmentModule(args[0] as PostsConfigurationDomain.TypeFeedDomain))
                    .build()

            ProfileBlackListFragmentComponent::class ->
                get<ProfileFragmentComponent>()
                    .blackListFragment
                    .init(ProfileBlackListFragmentModule(args[0] as BlackListFilter, args[1] as Int))
                    .build()

            ProfilePostsLikedFragmentComponent::class ->
                get<ProfileFragmentComponent>()
                    .likedFragment
                    .init(ProfilePostsFragmentModule(args[0] as PostsConfigurationDomain.TypeFeedDomain))
                    .build()

            InAppAuthActivityComponent::class -> get<UIComponent>().inAppAuthActivity.build()

            FingerprintAuthFragmentComponent::class ->
                get<InAppAuthActivityComponent>()
                    .fingerprintAuthFragmentComponent
                    .init(FingerprintAuthFragmentModule(args[0] as Int))
                    .build()

            PinCodeAuthFragmentComponent::class ->
                get<InAppAuthActivityComponent>()
                    .pinCodeAuthFragmentComponent
                    .init(PinCodeAuthFragmentModule(args[0] as Int))
                    .build()

            LoginActivityComponent::class -> get<UIComponent>().loginActivity.build()

            OnBoardingFragmentComponent::class ->
                get<LoginActivityComponent>()
                    .onBoardingFragmentComponent
                    .init(OnBoardingFragmentModule(args[0] as CyberName))
                    .build()

            MainActivityComponent::class -> get<UIComponent>().mainActivity.build()

            NotificationsFragmentComponent::class -> get<MainActivityComponent>().notificationsFragmentComponent.build()

            TrendingFeedFragmentComponent::class ->
                get<MainActivityComponent>().trendingFeedFragmentComponent
                    .init(TrendingFeedFragmentModule(args[0] as CommunityId, args[1] as CyberName))
                    .build()

            PostPageFragmentComponent::class ->
                get<UIComponent>()
                    .postPageFragment
                    .init(
                        PostPageFragmentModule(
                            args[0] as DiscussionIdModel,
                            args[1] as ContentId?
                        )
                    ).build()

            CommunitiesListFragmentComponent::class ->
                get<MainActivityComponent>()
                    .communitiesFragmentComponent
                    .init(CommunitiesListFragmentModule(args[0] as Boolean, args[1] as UserIdDomain, args[2] as Boolean))
                    .build()

            CommunitiesListFragmentTabComponent::class ->
                get<MainActivityComponent>()
                    .communitiesFragmentTabComponent
                    .init(CommunitiesListFragmentModule(args[0] as Boolean, args[1] as UserIdDomain, args[2] as Boolean))
                    .build()

            FeedbackActivityComponent::class -> get<UIComponent>().feedbackActivity.build()

            SelectCommunityDialogComponent::class -> get<UIComponent>().selectCommunityDialog.build()

            SubscriptionsFragmentComponent::class -> get<UIComponent>().subscriptionsFragment.build()

            FollowersFragmentComponent::class -> get<UIComponent>().followersFragment.build()

            CommunityPageFragmentComponent::class -> get<UIComponent>().communityPageFragment.build()

            CommunityPageAboutFragmentComponent::class -> get<UIComponent>()
                .communityPageAboutFragment
                .init(CommunityPageAboutFragmentModule(args[0] as String))
                .build()

            CommunityPageRulesFragmentComponent::class -> get<UIComponent>()
                .communityPageRulesFragment
                .init(CommunityPageRulesFragmentModule(args[0] as String))
                .build()

            CommunityPageAboutFragmentComponent::class -> get<UIComponent>()
                .communityPageAboutFragment
                .init(CommunityPageAboutFragmentModule(args[0] as String))
                .build()

            CommunityPageLeadsListComponent::class -> get<CommunityPageFragmentComponent>()
                .leadsListFragment
                .init(CommunityPageLeadsListModule(args[0] as String))
                .build()

            FeedFragmentComponent::class -> get<UIComponent>()
                .feedFragment
                .build()

            PostFiltersFragmentComponent::class -> get<UIComponent>()
                .postFiltersFragment
                .build()

            PostReportFragmentComponent::class -> get<UIComponent>()
                .postReportFragment
                .init(PostReportModule(args[0] as ContentId))
                .build()

            MyFeedFragmentComponent::class -> get<UIComponent>()
                .postsListFragment
                .build()

            ProfilePostsFragmentComponent::class ->
                get<ProfileFragmentComponent>()
                .profilePostsFragment
                .init(ProfilePostsFragmentModule(args[0] as PostsConfigurationDomain.TypeFeedDomain))
                .build()

            FtueFragmentComponent::class -> get<UIComponent>()
                .ftueFragment
                .build()

            FtueSearchCommunityFragmentComponent::class -> get<UIComponent>()
                .ftueSearchCommunityFragment
                .build()

            FtueFinishFragmentComponent::class -> get<UIComponent>()
                .ftueFinishFragmentComponent
                .build()

            DashboardFragmentComponent::class -> get<UIComponent>()
                .dashboardFragmentComponent
                .build()

            SignInUserNameFragmentComponent::class -> get<LoginActivityComponent>()
                .signInUserNameFragmentComponent
                .build()

            SignInQrCodeFragmentComponent::class -> get<LoginActivityComponent>()
                .signInQrCodeFragmentComponent

            ProfileCommentsFragmentComponent::class -> get<UIComponent>()
                .profileCommentsFragmentComponent
                .init(ProfileCommentsModule(args[0] as UserIdDomain))
                .build()

            else -> throw UnsupportedOperationException("This component is not supported: ${type.simpleName}")
        } as T
    }
}