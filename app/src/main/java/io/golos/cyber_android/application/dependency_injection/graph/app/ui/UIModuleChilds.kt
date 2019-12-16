package io.golos.cyber_android.application.dependency_injection.graph.app.ui

import dagger.Module
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.bio_fragment.BioFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.dialogs.select_community_dialog.SelectCommunityDialogComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.edit_profile_avatar_activity.EditProfileAvatarActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.edit_profile_cover_activity.EditProfileCoverActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.editor_page_fragment.EditorPageFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.feedback_activity.FeedbackActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.InAppAuthActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity.LoginActivityComponent
import io.golos.cyber_android.ui.screens.main_activity.di.MainActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.ui.screens.profile.new_profile.di.ProfileFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_settings_activity.ProfileSettingsActivityComponent
import io.golos.cyber_android.ui.screens.profile.new_profile.di.ProfileExternalUserFragmentComponent
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsFragmentComponent
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsLikedFragmentComponent

@Module(subcomponents = [
    BioFragmentComponent::class,
    EditProfileAvatarActivityComponent::class,
    EditProfileCoverActivityComponent::class,
    EditorPageFragmentComponent::class,
    LoginActivityComponent::class,
    MainActivityComponent::class,
    PostPageFragmentComponent::class,
    ProfileSettingsActivityComponent::class,
    InAppAuthActivityComponent::class,
    FeedbackActivityComponent::class,
    SelectCommunityDialogComponent::class,
    ProfileFragmentComponent::class,
    ProfileExternalUserFragmentComponent::class
])
class UIModuleChilds