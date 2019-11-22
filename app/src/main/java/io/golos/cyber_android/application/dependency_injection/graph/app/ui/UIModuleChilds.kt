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
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.MainActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.ProfileFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_settings_activity.ProfileSettingsActivityComponent

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
    ProfileFragmentComponent::class
])
class UIModuleChilds