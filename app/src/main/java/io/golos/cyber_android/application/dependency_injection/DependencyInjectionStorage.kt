package io.golos.cyber_android.application.dependency_injection

import android.content.Context
import io.golos.cyber_android.application.dependency_injection.graph.app.AppComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.AppModule
import io.golos.cyber_android.application.dependency_injection.graph.app.DaggerAppComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.UIComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.bio_fragment.BioFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.bio_fragment.BioFragmentModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.edit_profile_avatar_activity.EditProfileAvatarActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.edit_profile_avatar_activity.EditProfileAvatarActivityModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.edit_profile_cover_activity.EditProfileCoverActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.edit_profile_cover_activity.EditProfileCoverActivityModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.editor_page_fragment.EditorPageFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.editor_page_fragment.EditorPageFragmentModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.feedback_activity.FeedbackActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.InAppAuthActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.fingerprint_auth_fragment.FingerprintAuthFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.fingerprint_auth_fragment.FingerprintAuthFragmentModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.pin_code_auth_fragment.PinCodeAuthFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.pin_code_auth_fragment.PinCodeAuthFragmentModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity.LoginActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity.on_boarding.OnBoardingFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity.on_boarding.OnBoardingFragmentModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.MainActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.CommunitiesFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.discover_fragment.DiscoverFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.feed_fragment.MyFeedFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.feed_fragment.MyFeedFragmentModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.notifications_fragment.NotificationsFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.profile_fragment.ProfileFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.profile_fragment.ProfileFragmentModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.trending_feed.TrendingFeedFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.trending_feed.TrendingFeedFragmentModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.user_posts_feed.UserPostsFeedFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.user_posts_feed.UserPostsFeedFragmentModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentModule
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_settings_activity.ProfileSettingsActivityComponent
import io.golos.domain.entities.CyberUser
import io.golos.domain.interactors.model.CommunityId
import io.golos.domain.interactors.model.CommunityModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.sharedmodel.CyberName
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
        return when(type) {
            AppComponent::class -> DaggerAppComponent.builder().appModule(AppModule(appContext)).build()

            UIComponent::class -> get<AppComponent>().ui.build()

            BioFragmentComponent::class ->
                get<UIComponent>()
                    .bioFragment
                    .init(BioFragmentModule(args[0] as CyberName))
                    .build()

            EditProfileAvatarActivityComponent::class ->
                get<UIComponent>()
                    .editProfileAvatarActivity
                    .init(EditProfileAvatarActivityModule(args[0] as CyberName))
                    .build()

            EditProfileCoverActivityComponent::class ->
                get<UIComponent>()
                    .editProfileCoverActivity
                    .init(EditProfileCoverActivityModule(args[0] as CyberName))
                    .build()

            EditorPageFragmentComponent::class ->
                get<UIComponent>()
                    .editorPageFragment
                    .init(EditorPageFragmentModule(args[0] as CommunityModel?, args[1] as DiscussionIdModel?))
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

            MyFeedFragmentComponent::class ->
                get<MainActivityComponent>()
                    .myFeedFragmentComponent
                    .init(MyFeedFragmentModule(args[0] as CyberUser, args[1] as CyberName))
                    .build()

            NotificationsFragmentComponent::class -> get<MainActivityComponent>().notificationsFragmentComponent.build()

            ProfileFragmentComponent::class ->
                get<MainActivityComponent>()
                    .profileFragmentComponent
                    .init(ProfileFragmentModule(args[0] as CyberName))
                    .build()

            TrendingFeedFragmentComponent::class ->
                get<MainActivityComponent>().trendingFeedFragmentComponent
                    .init(TrendingFeedFragmentModule(args[0] as CommunityId, args[1] as CyberName))
                    .build()

            UserPostsFeedFragmentComponent::class ->
                get<MainActivityComponent>()
                    .userPostsFeedFragmentComponent
                    .init(UserPostsFeedFragmentModule(args[0] as CyberUser))
                    .build()

            PostPageFragmentComponent::class ->
                get<UIComponent>()
                    .postPageFragment
                    .init(PostPageFragmentModule(args[0] as DiscussionIdModel))
                    .build()

            CommunitiesFragmentComponent::class -> get<MainActivityComponent>().communitiesFragmentComponent.build()

            DiscoverFragmentComponent::class -> get<CommunitiesFragmentComponent>().discoverFragmentComponent.build()

            ProfileSettingsActivityComponent::class -> get<UIComponent>().profileSettingsActivity.build()

            FeedbackActivityComponent::class -> get<UIComponent>().feedbackActivity.build()

            else -> throw UnsupportedOperationException("This component is not supported: ${type.simpleName}")
        } as T
    }
}