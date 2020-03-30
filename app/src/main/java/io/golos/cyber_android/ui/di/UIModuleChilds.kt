package io.golos.cyber_android.ui.di

import dagger.Module
import io.golos.cyber_android.ui.dialogs.select_community_dialog.di.SelectCommunityDialogComponent
import io.golos.cyber_android.ui.screens.app_start.sign_in.activity.di.SignInActivityComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.activity.di.SignUpActivityComponent
import io.golos.cyber_android.ui.screens.in_app_auth_activity.di.InAppAuthActivityComponent
import io.golos.cyber_android.ui.screens.app_start.welcome.activity.di.WelcomeActivityComponent
import io.golos.cyber_android.ui.screens.main_activity.di.MainActivityComponent
import io.golos.cyber_android.ui.screens.post_edit.activity.di.EditorPageActivityComponent
import io.golos.cyber_android.ui.screens.post_view.di.PostPageFragmentComponent
import io.golos.cyber_android.ui.screens.profile.di.ProfileFragmentComponent
import io.golos.cyber_android.ui.screens.profile.di.ProfileExternalUserFragmentComponent

@Module(subcomponents = [
    WelcomeActivityComponent::class,
    SignInActivityComponent::class,
    SignUpActivityComponent::class,
    MainActivityComponent::class,
    PostPageFragmentComponent::class,
    InAppAuthActivityComponent::class,
    SelectCommunityDialogComponent::class,
    ProfileFragmentComponent::class,
    ProfileExternalUserFragmentComponent::class,
    EditorPageActivityComponent::class
])
class UIModuleChilds