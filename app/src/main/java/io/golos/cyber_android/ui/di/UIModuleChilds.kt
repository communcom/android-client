package io.golos.cyber_android.ui.di

import dagger.Module
import io.golos.cyber_android.ui.screens.login_sign_up_bio.di.BioFragmentComponent
import io.golos.cyber_android.ui.dialogs.select_community_dialog.di.SelectCommunityDialogComponent
import io.golos.cyber_android.ui.screens.post_edit.di.EditorPageFragmentComponent
import io.golos.cyber_android.ui.screens.feedback_activity.di.FeedbackActivityComponent
import io.golos.cyber_android.ui.screens.in_app_auth_activity.di.InAppAuthActivityComponent
import io.golos.cyber_android.ui.screens.login_activity.di.LoginActivityComponent
import io.golos.cyber_android.ui.screens.main_activity.di.MainActivityComponent
import io.golos.cyber_android.ui.screens.post_view.di.PostPageFragmentComponent
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