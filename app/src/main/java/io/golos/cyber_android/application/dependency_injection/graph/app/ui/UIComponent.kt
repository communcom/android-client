package io.golos.cyber_android.application.dependency_injection.graph.app.ui

import dagger.Subcomponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.bio_fragment.BioFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.dialogs.select_community_dialog.SelectCommunityDialogComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.edit_profile_avatar_activity.EditProfileAvatarActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.edit_profile_cover_activity.EditProfileCoverActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.editor_page_fragment.EditorPageFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.feedback_activity.FeedbackActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.InAppAuthActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity.LoginActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.MainActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_settings_activity.ProfileSettingsActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.followers.FollowersFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.subscriptions.SubscriptionsFragmentComponent
import io.golos.cyber_android.ui.common.widgets.pin.PinDigit
import io.golos.cyber_android.ui.shared_fragments.post.view.view_holders.post_text.widgets.AttachmentsWidget
import io.golos.cyber_android.ui.shared_fragments.post.view.view_holders.post_text.widgets.EmbedWebsiteWidget
import io.golos.cyber_android.ui.shared_fragments.post.view.view_holders.post_text.widgets.ParagraphWidget
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
    val editProfileAvatarActivity: EditProfileAvatarActivityComponent.Builder
    val editProfileCoverActivity: EditProfileCoverActivityComponent.Builder
    val editorPageFragment: EditorPageFragmentComponent.Builder
    val loginActivity: LoginActivityComponent.Builder
    val mainActivity: MainActivityComponent.Builder
    val postPageFragment: PostPageFragmentComponent.Builder
    val profileSettingsActivity: ProfileSettingsActivityComponent.Builder
    val inAppAuthActivity: InAppAuthActivityComponent.Builder
    val feedbackActivity: FeedbackActivityComponent.Builder
    val selectCommunityDialog: SelectCommunityDialogComponent.Builder
    val subscriptionsFragment: SubscriptionsFragmentComponent.Builder
    val followersFragment: FollowersFragmentComponent.Builder

    fun inject(pinDigit: PinDigit)

    fun inject(widget: ParagraphWidget)
    fun inject(widget: EmbedWebsiteWidget)
    fun inject(widget: AttachmentsWidget)
}