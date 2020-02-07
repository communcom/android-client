package io.golos.cyber_android.application.di_storage

import android.content.Context
import io.golos.cyber_android.application.di.AppComponent
import io.golos.cyber_android.application.di.AppModule
import io.golos.cyber_android.application.di.DaggerAppComponent
import io.golos.cyber_android.ui.di.UIComponent
import io.golos.cyber_android.ui.dialogs.select_community_dialog.di.SelectCommunityDialogComponent
import io.golos.cyber_android.ui.dto.*
import io.golos.cyber_android.ui.screens.communities_list.di.CommunitiesListFragmentComponent
import io.golos.cyber_android.ui.screens.communities_list.di.CommunitiesListFragmentModule
import io.golos.cyber_android.ui.screens.communities_list.di.CommunitiesListFragmentTabComponent
import io.golos.cyber_android.ui.screens.community_page.di.CommunityPageFragmentComponent
import io.golos.cyber_android.ui.screens.community_page.dto.CommunityFriend
import io.golos.cyber_android.ui.screens.community_page_about.di.CommunityPageAboutFragmentComponent
import io.golos.cyber_android.ui.screens.community_page_about.di.CommunityPageAboutFragmentModule
import io.golos.cyber_android.ui.screens.community_page_friends.di.CommunityPageFriendsComponent
import io.golos.cyber_android.ui.screens.community_page_friends.di.CommunityPageFriendsModule
import io.golos.cyber_android.ui.screens.community_page_leaders_list.di.CommunityPageLeadsListComponent
import io.golos.cyber_android.ui.screens.community_page_leaders_list.di.CommunityPageLeadsListModule
import io.golos.cyber_android.ui.screens.community_page_members.di.CommunityPageMembersComponent
import io.golos.cyber_android.ui.screens.community_page_members.di.CommunityPageMembersModule
import io.golos.cyber_android.ui.screens.community_page_post.di.CommunityPostFragmentComponent
import io.golos.cyber_android.ui.screens.community_page_post.di.CommunityPostFragmentModule
import io.golos.cyber_android.ui.screens.community_page_rules.di.CommunityPageRulesFragmentComponent
import io.golos.cyber_android.ui.screens.community_page_rules.di.CommunityPageRulesFragmentModule
import io.golos.cyber_android.ui.screens.dashboard.di.DashboardFragmentComponent
import io.golos.cyber_android.ui.screens.feed.di.FeedFragmentComponent
import io.golos.cyber_android.ui.screens.feed_my.di.MyFeedFragmentComponent
import io.golos.cyber_android.ui.screens.feedback_activity.di.FeedbackActivityComponent
import io.golos.cyber_android.ui.screens.ftue.di.FtueFragmentComponent
import io.golos.cyber_android.ui.screens.ftue_finish.di.FtueFinishFragmentComponent
import io.golos.cyber_android.ui.screens.ftue_search_community.di.FtueSearchCommunityFragmentComponent
import io.golos.cyber_android.ui.screens.in_app_auth_activity.di.InAppAuthActivityComponent
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.di.FingerprintAuthFragmentComponent
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.di.FingerprintAuthFragmentModule
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.di.PinCodeAuthFragmentComponent
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.di.PinCodeAuthFragmentModule
import io.golos.cyber_android.ui.screens.login_activity.di.LoginActivityComponent
import io.golos.cyber_android.ui.screens.login_sign_in_qr_code.di.SignInQrCodeFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_in_username.di.SignInUserNameFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_up_countries.di.SignUpCountryComponent
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.di.SignUpProtectionKeysFragmentComponent
import io.golos.cyber_android.ui.screens.main_activity.di.MainActivityComponent
import io.golos.cyber_android.ui.screens.notifications.di.NotificationsFragmentComponent
import io.golos.cyber_android.ui.screens.post_edit.activity.di.EditorPageActivityComponent
import io.golos.cyber_android.ui.screens.post_edit.fragment.di.EditorPageFragmentComponent
import io.golos.cyber_android.ui.screens.post_edit.fragment.di.EditorPageFragmentModule
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersHolder
import io.golos.cyber_android.ui.screens.post_filters.di.PostFilterFragmentModule
import io.golos.cyber_android.ui.screens.post_filters.di.PostFiltersFragmentComponent
import io.golos.cyber_android.ui.screens.post_report.di.PostReportFragmentComponent
import io.golos.cyber_android.ui.screens.post_report.di.PostReportModule
import io.golos.cyber_android.ui.screens.post_view.di.PostPageFragmentComponent
import io.golos.cyber_android.ui.screens.post_view.di.PostPageFragmentModule
import io.golos.cyber_android.ui.screens.profile.di.ProfileExternalUserFragmentComponent
import io.golos.cyber_android.ui.screens.profile.di.ProfileFragmentComponent
import io.golos.cyber_android.ui.screens.profile.di.ProfileFragmentModule
import io.golos.cyber_android.ui.screens.profile_bio.di.ProfileBioFragmentComponent
import io.golos.cyber_android.ui.screens.profile_bio.di.ProfileBioFragmentModule
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
import io.golos.cyber_android.ui.screens.profile_photos.di.ProfilePhotosFragmentComponent
import io.golos.cyber_android.ui.screens.profile_photos.di.ProfilePhotosFragmentModule
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsExternalUserFragmentComponent
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsFragmentComponent
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsFragmentModule
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsLikedFragmentComponent
import io.golos.cyber_android.ui.screens.subscriptions.di.SubscriptionsFragmentComponent
import io.golos.cyber_android.ui.screens.wallet.di.WalletFragmentComponent
import io.golos.cyber_android.ui.screens.wallet.di.WalletFragmentModule
import io.golos.domain.dto.PostsConfigurationDomain
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.utils.IdUtil
import kotlin.reflect.KClass

/** Storage for Dagger components on application level  */
class DependencyInjectionStorage(private val appContext: Context) {

    private val components = mutableMapOf<KClass<*>, MutableMap<String, Any>>()

    inline fun <reified T> get(key: String, vararg args: Any?): T = getComponent(key, T::class, args)

    inline fun <reified T> release(key: String) = releaseComponent(key, T::class)

    @Suppress("UNCHECKED_CAST")
    fun <T> getComponent(key: String, type: KClass<*>, args: Array<out Any?>): T {
        val componentsSet = components[type]

        return if(componentsSet == null) {
            val component = provideComponent<T>(type, args)
            components[type] = mutableMapOf(key to component as Any)

            component
        } else {
            var component = componentsSet[key]
            if(component != null) {
                component as T
            }

            component = provideComponent<T>(type, args)
            componentsSet[key] = component as Any
            component
        }
    }

    fun releaseComponent(key: String, type: KClass<*>) {
        val componentsSet = components[type] ?: return

        componentsSet.remove(key)

        if(componentsSet.isEmpty()) {
            components.remove(type)
        }
    }

    inline fun <reified T> getBase(): T = getBaseComponent(T::class)

    @Suppress("UNCHECKED_CAST")
    fun <T> getBaseComponent(type: KClass<*>): T {
        val componentsSet = components[type]

        return if(componentsSet != null) {
            componentsSet.entries.first().value as T
        } else {
            val component = provideComponent<T>(type, arrayOfNulls(0))
            components[type] = mutableMapOf(IdUtil.generateStringId() to component as Any)

            component
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    private fun <T> provideComponent(type: KClass<*>, args: Array<out Any?>): T {
        @Suppress("EXPERIMENTAL_API_USAGE")
        return when (type) {
            AppComponent::class -> DaggerAppComponent.builder().appModule(AppModule(appContext)).build()

            UIComponent::class -> getBase<AppComponent>().ui.build()

            EditorPageActivityComponent::class -> getBase<UIComponent>().editorPageActivity.build()

            EditorPageFragmentComponent::class ->
                getBase<EditorPageActivityComponent>()
                    .editorPageFragment
                    .init(
                        EditorPageFragmentModule(args[0] as ContentId?)
                    )
                    .build()

            ProfileFragmentComponent::class ->
                getBase<UIComponent>()
                    .profileFragment
                    .init(ProfileFragmentModule(args[0] as UserIdDomain))
                    .build()

            ProfileExternalUserFragmentComponent::class ->
                getBase<UIComponent>()
                    .profileExternalUserFragment
                    .init(ProfileFragmentModule(args[0] as UserIdDomain))
                    .build()

            ProfilePhotosFragmentComponent::class ->
                getBase<ProfileFragmentComponent>()
                    .photosFragment
                    .init(ProfilePhotosFragmentModule(args[0] as ProfileItem, args[1] as String?))
                    .build()

            ProfileBioFragmentComponent::class ->
                getBase<ProfileFragmentComponent>()
                    .bioFragment
                    .init(ProfileBioFragmentModule(args[0] as String?))
                    .build()

            ProfileCommunitiesFragmentComponent::class ->
                getBase<ProfileFragmentComponent>()
                    .communitiesFragment
                    .init(ProfileCommunitiesFragmentModule(args[0] as ProfileCommunities))
                    .build()

            ProfileCommunitiesExternalUserFragmentComponent::class ->
                getBase<ProfileExternalUserFragmentComponent>()
                    .communitiesFragment
                    .init(ProfileCommunitiesFragmentModule(args[0] as ProfileCommunities))
                    .build()

            ProfileFollowersFragmentComponent::class ->
                getBase<ProfileFragmentComponent>()
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
                getBase<ProfileExternalUserFragmentComponent>()
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
                getBase<ProfileExternalUserFragmentComponent>()
                    .profilePostsFragment
                    .init(ProfilePostsFragmentModule(args[0] as PostsConfigurationDomain.TypeFeedDomain))
                    .build()

            ProfileBlackListFragmentComponent::class ->
                getBase<ProfileFragmentComponent>()
                    .blackListFragment
                    .init(ProfileBlackListFragmentModule(args[0] as BlackListFilter, args[1] as Int))
                    .build()

            ProfilePostsLikedFragmentComponent::class ->
                getBase<ProfileFragmentComponent>()
                    .likedFragment
                    .init(ProfilePostsFragmentModule(args[0] as PostsConfigurationDomain.TypeFeedDomain))
                    .build()

            InAppAuthActivityComponent::class -> getBase<UIComponent>().inAppAuthActivity.build()

            FingerprintAuthFragmentComponent::class ->
                getBase<InAppAuthActivityComponent>()
                    .fingerprintAuthFragmentComponent
                    .init(FingerprintAuthFragmentModule(args[0] as Int))
                    .build()

            PinCodeAuthFragmentComponent::class ->
                getBase<InAppAuthActivityComponent>()
                    .pinCodeAuthFragmentComponent
                    .init(PinCodeAuthFragmentModule(args[0] as Int))
                    .build()

            LoginActivityComponent::class -> getBase<UIComponent>().loginActivity.build()

            MainActivityComponent::class -> getBase<UIComponent>().mainActivity.build()

            PostPageFragmentComponent::class ->
                getBase<UIComponent>()
                    .postPageFragment
                    .init(
                        PostPageFragmentModule(
                            args[0] as DiscussionIdModel,
                            args[1] as ContentId
                        )
                    ).build()

            CommunitiesListFragmentComponent::class ->
                getBase<MainActivityComponent>()
                    .communitiesFragmentComponent
                    .init(CommunitiesListFragmentModule(args[0] as Boolean, args[1] as UserIdDomain, args[2] as Boolean))
                    .build()

            CommunitiesListFragmentTabComponent::class ->
                getBase<MainActivityComponent>()
                    .communitiesFragmentTabComponent
                    .init(CommunitiesListFragmentModule(args[0] as Boolean, args[1] as UserIdDomain, args[2] as Boolean))
                    .build()

            WalletFragmentComponent::class ->
                getBase<MainActivityComponent>()
                    .walletFragmentComponent
                    .init(WalletFragmentModule(args[0] as List<WalletCommunityBalanceRecordDomain>))
                    .build()

            FeedbackActivityComponent::class -> getBase<UIComponent>().feedbackActivity.build()

            SelectCommunityDialogComponent::class -> getBase<UIComponent>().selectCommunityDialog.build()

            SubscriptionsFragmentComponent::class -> getBase<UIComponent>().subscriptionsFragment.build()

            CommunityPageFragmentComponent::class -> getBase<UIComponent>().communityPageFragment.build()

            CommunityPageAboutFragmentComponent::class ->
                getBase<UIComponent>()
                    .communityPageAboutFragment
                    .init(CommunityPageAboutFragmentModule(args[0] as String))
                    .build()

            CommunityPageRulesFragmentComponent::class ->
                getBase<UIComponent>()
                    .communityPageRulesFragment
                    .init(CommunityPageRulesFragmentModule(args[0] as String))
                    .build()

            CommunityPageAboutFragmentComponent::class ->
                getBase<UIComponent>()
                    .communityPageAboutFragment
                    .init(CommunityPageAboutFragmentModule(args[0] as String))
                    .build()

            CommunityPageLeadsListComponent::class ->
                getBase<CommunityPageFragmentComponent>()
                    .leadsListFragment
                    .init(CommunityPageLeadsListModule(args[0] as String))
                    .build()

            CommunityPageMembersComponent::class ->
                getBase<CommunityPageFragmentComponent>()
                    .membersFragment
                    .init(CommunityPageMembersModule(args[0] as String, args[1] as Int))
                    .build()

            CommunityPageFriendsComponent::class ->
                getBase<CommunityPageFragmentComponent>()
                    .friendsFragment
                    .init(CommunityPageFriendsModule(args[0] as List<CommunityFriend>))
                    .build()

            FeedFragmentComponent::class -> getBase<UIComponent>().feedFragment.build()

            PostFiltersFragmentComponent::class ->
                getBase<UIComponent>()
                    .postFiltersFragment
                    .init(
                        PostFilterFragmentModule(
                            args[0] as Boolean,
                            args[1] as PostFiltersHolder.UpdateTimeFilter?,
                            args[2] as PostFiltersHolder.PeriodTimeFilter?
                        )
                    )
                    .build()

            PostReportFragmentComponent::class ->
                getBase<UIComponent>()
                    .postReportFragment
                    .init(PostReportModule(args[0] as ContentId))
                    .build()

            MyFeedFragmentComponent::class -> getBase<UIComponent>().postsListFragment.build()

            CommunityPostFragmentComponent::class ->
                getBase<UIComponent>()
                    .communityPostFragment
                    .init(CommunityPostFragmentModule(args[0] as String, args[1] as String?))
                    .build()

            ProfilePostsFragmentComponent::class ->
                getBase<ProfileFragmentComponent>()
                    .profilePostsFragment
                    .init(ProfilePostsFragmentModule(args[0] as PostsConfigurationDomain.TypeFeedDomain))
                    .build()

            FtueFragmentComponent::class -> getBase<UIComponent>().ftueFragment.build()

            FtueSearchCommunityFragmentComponent::class -> getBase<UIComponent>().ftueSearchCommunityFragment.build()

            FtueFinishFragmentComponent::class -> getBase<UIComponent>().ftueFinishFragmentComponent.build()

            DashboardFragmentComponent::class -> getBase<UIComponent>().dashboardFragmentComponent.build()

            SignInUserNameFragmentComponent::class ->
                getBase<LoginActivityComponent>()
                    .signInUserNameFragmentComponent
                    .build()

            SignInQrCodeFragmentComponent::class ->
                getBase<LoginActivityComponent>()
                    .signInQrCodeFragmentComponent
                    .build()

            SignUpCountryComponent::class ->
                getBase<LoginActivityComponent>()
                    .signUpCountryComponent
                    .build()

            SignUpProtectionKeysFragmentComponent::class ->
                getBase<LoginActivityComponent>()
                    .signUpProtectionKeysFragmentComponent
                    .build()

            ProfileCommentsFragmentComponent::class ->
                getBase<UIComponent>()
                    .profileCommentsFragmentComponent
                    .init(ProfileCommentsModule(args[0] as UserIdDomain))
                    .build()

            NotificationsFragmentComponent::class ->
                getBase<UIComponent>()
                    .notificationsFragmentComponent
                    .build()

            else -> throw UnsupportedOperationException("This component is not supported: ${type.simpleName}")
        } as T
    }
}