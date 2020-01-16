package io.golos.cyber_android.application.di_storage

import android.content.Context
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.cyber_android.application.di.AppComponent
import io.golos.cyber_android.application.di.AppModule
import io.golos.cyber_android.application.di.DaggerAppComponent
import io.golos.cyber_android.ui.di.UIComponent
import io.golos.cyber_android.ui.screens.login_sign_up_bio.di.BioFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_up_bio.di.BioFragmentModule
import io.golos.cyber_android.ui.screens.community_page.di.CommunityPageFragmentComponent
import io.golos.cyber_android.ui.screens.community_page_leaders_list.di.CommunityPageLeadsListComponent
import io.golos.cyber_android.ui.screens.community_page_leaders_list.di.CommunityPageLeadsListModule
import io.golos.cyber_android.ui.screens.community_page_about.di.CommunityPageAboutFragmentComponent
import io.golos.cyber_android.ui.screens.community_page_about.di.CommunityPageAboutFragmentModule
import io.golos.cyber_android.ui.screens.community_page_rules.di.CommunityPageRulesFragmentComponent
import io.golos.cyber_android.ui.screens.community_page_rules.di.CommunityPageRulesFragmentModule
import io.golos.cyber_android.ui.dialogs.select_community_dialog.di.SelectCommunityDialogComponent
import io.golos.cyber_android.ui.screens.post_edit.di.EditorPageFragmentComponent
import io.golos.cyber_android.ui.screens.post_edit.di.EditorPageFragmentModule
import io.golos.cyber_android.ui.screens.feed.di.FeedFragmentComponent
import io.golos.cyber_android.ui.screens.feedback_activity.di.FeedbackActivityComponent
import io.golos.cyber_android.ui.screens.followers.di.FollowersFragmentComponent
import io.golos.cyber_android.ui.screens.in_app_auth_activity.di.InAppAuthActivityComponent
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.di.FingerprintAuthFragmentComponent
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.di.FingerprintAuthFragmentModule
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.di.PinCodeAuthFragmentComponent
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.di.PinCodeAuthFragmentModule
import io.golos.cyber_android.ui.screens.post_filters.di.PostFilterFragmentModule
import io.golos.cyber_android.ui.screens.post_filters.di.PostFiltersFragmentComponent
import io.golos.cyber_android.ui.screens.post_view.di.PostPageFragmentComponent
import io.golos.cyber_android.ui.screens.post_view.di.PostPageFragmentModule
import io.golos.cyber_android.ui.screens.profile_bio.di.ProfileBioFragmentComponent
import io.golos.cyber_android.ui.screens.profile_bio.di.ProfileBioFragmentModule
import io.golos.cyber_android.ui.screens.profile_photos.di.ProfilePhotosFragmentComponent
import io.golos.cyber_android.ui.screens.profile_photos.di.ProfilePhotosFragmentModule
import io.golos.cyber_android.ui.screens.subscriptions.di.SubscriptionsFragmentComponent
import io.golos.cyber_android.ui.dto.*
import io.golos.cyber_android.ui.screens.communities_list.di.CommunitiesListFragmentComponent
import io.golos.cyber_android.ui.screens.communities_list.di.CommunitiesListFragmentModule
import io.golos.cyber_android.ui.screens.communities_list.di.CommunitiesListFragmentTabComponent
import io.golos.cyber_android.ui.screens.community_page_members.di.CommunityPageMembersComponent
import io.golos.cyber_android.ui.screens.community_page_members.di.CommunityPageMembersModule
import io.golos.cyber_android.ui.screens.community_page_post.di.CommunityPostFragmentComponent
import io.golos.cyber_android.ui.screens.community_page_post.di.CommunityPostFragmentModule
import io.golos.cyber_android.ui.screens.dashboard.di.DashboardFragmentComponent
import io.golos.cyber_android.ui.screens.ftue.di.FtueFragmentComponent
import io.golos.cyber_android.ui.screens.ftue_finish.di.FtueFinishFragmentComponent
import io.golos.cyber_android.ui.screens.ftue_search_community.di.FtueSearchCommunityFragmentComponent
import io.golos.cyber_android.ui.screens.login_activity.di.LoginActivityComponent
import io.golos.cyber_android.ui.screens.login_activity.di.on_boarding.OnBoardingFragmentComponent
import io.golos.cyber_android.ui.screens.login_activity.di.on_boarding.OnBoardingFragmentModule
import io.golos.cyber_android.ui.screens.login_sign_in_qr_code.di.SignInQrCodeFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_in_username.di.SignInUserNameFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.di.SignUpProtectionKeysFragmentComponent
import io.golos.cyber_android.ui.screens.main_activity.di.MainActivityComponent
import io.golos.cyber_android.ui.screens.feed_my.di.MyFeedFragmentComponent
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersHolder
import io.golos.cyber_android.ui.screens.post_report.di.PostReportFragmentComponent
import io.golos.cyber_android.ui.screens.post_report.di.PostReportModule
import io.golos.cyber_android.ui.screens.profile.di.ProfileExternalUserFragmentComponent
import io.golos.cyber_android.ui.screens.profile.di.ProfileFragmentComponent
import io.golos.cyber_android.ui.screens.profile.di.ProfileFragmentModule
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
import io.golos.domain.dto.PostsConfigurationDomain
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlin.reflect.KClass

/** Storage for Dagger components on application level  */
class DependencyInjectionStorage(private val appContext: Context) {

    private val components = mutableMapOf<KClass<*>, Any>()

    inline fun <reified T> get(vararg args: Any?): T = getComponent(T::class, args)

    inline fun <reified T> release() = releaseComponent(T::class)

    @Suppress("UNCHECKED_CAST")
    fun <T> getComponent(type: KClass<*>, args: Array<out Any?>): T {
        var result = components[type]
        if (result == null) {
            result = provideComponent<T>(type, args)
            components[type] = result!!
        }
        return result as T
    }

    fun releaseComponent(type: KClass<*>) = components.remove(type)

    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    private fun <T> provideComponent(type: KClass<*>, args: Array<out Any?>): T {
        @Suppress("EXPERIMENTAL_API_USAGE")
        return when (type) {
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
                .init(
                    CommunityPageRulesFragmentModule(
                        args[0] as String
                    )
                )
                .build()

            CommunityPageAboutFragmentComponent::class -> get<UIComponent>()
                .communityPageAboutFragment
                .init(CommunityPageAboutFragmentModule(args[0] as String))
                .build()

            CommunityPageLeadsListComponent::class -> get<CommunityPageFragmentComponent>()
                .leadsListFragment
                .init(CommunityPageLeadsListModule(args[0] as String))
                .build()

            CommunityPageMembersComponent::class -> get<CommunityPageFragmentComponent>()
                .membersFragment
                .init(CommunityPageMembersModule(args[0] as String, args[1] as Int))
                .build()

            FeedFragmentComponent::class -> get<UIComponent>()
                .feedFragment
                .build()

            PostFiltersFragmentComponent::class -> get<UIComponent>()
                .postFiltersFragment
                .init(
                    PostFilterFragmentModule(
                        args[0] as Boolean,
                        args[1] as PostFiltersHolder.UpdateTimeFilter?,
                        args[2] as PostFiltersHolder.PeriodTimeFilter?
                    )
                )
                .build()

            PostReportFragmentComponent::class -> get<UIComponent>()
                .postReportFragment
                .init(PostReportModule(args[0] as ContentId))
                .build()

            MyFeedFragmentComponent::class -> get<UIComponent>()
                .postsListFragment
                .build()

            CommunityPostFragmentComponent::class -> get<UIComponent>()
                .communityPostFragment
                .init(
                    CommunityPostFragmentModule(args[0] as String, args[1] as String?)
                )
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
                .build()

            SignUpProtectionKeysFragmentComponent::class -> get<LoginActivityComponent>()
                .signUpProtectionKeysFragmentComponent
                .build()

            ProfileCommentsFragmentComponent::class -> get<UIComponent>()
                .profileCommentsFragmentComponent
                .init(ProfileCommentsModule(args[0] as UserIdDomain))
                .build()

            else -> throw UnsupportedOperationException("This component is not supported: ${type.simpleName}")
        } as T
    }
}