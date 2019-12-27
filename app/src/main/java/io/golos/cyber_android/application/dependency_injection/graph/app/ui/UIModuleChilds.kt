package io.golos.cyber_android.application.dependency_injection.graph.app.ui

import dagger.Module
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.bio_fragment.BioFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.dialogs.select_community_dialog.SelectCommunityDialogComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.editor_page_fragment.EditorPageFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.feedback_activity.FeedbackActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.InAppAuthActivityComponent
import io.golos.cyber_android.ui.screens.login_activity.di.LoginActivityComponent
import io.golos.cyber_android.ui.screens.main_activity.di.MainActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.ui.screens.profile.di.ProfileFragmentComponent
import io.golos.cyber_android.ui.screens.profile.di.ProfileExternalUserFragmentComponent

@Module(subcomponents = [
    BioFragmentComponent::class,
    EditorPageFragmentComponent::class,
    LoginActivityComponent::class,
    MainActivityComponent::class,
    PostPageFragmentComponent::class,
    InAppAuthActivityComponent::class,
    FeedbackActivityComponent::class,
    SelectCommunityDialogComponent::class,
    ProfileFragmentComponent::class,
    ProfileExternalUserFragmentComponent::class
])
class UIModuleChilds