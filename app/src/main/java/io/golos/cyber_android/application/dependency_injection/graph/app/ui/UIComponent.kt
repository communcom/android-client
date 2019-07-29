package io.golos.cyber_android.application.dependency_injection.graph.app.ui

import dagger.Subcomponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.bio_fragment.BioFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.edit_profile_avatar_activity.EditProfileAvatarActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.edit_profile_cover_activity.EditProfileCoverActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.editor_page_fragment.EditorPageFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity.LoginActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.MainActivityComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_settings_activity.ProfileSettingsActivityComponent
import io.golos.domain.dependency_injection.scopes.UIScope

@Subcomponent(modules = [
    UIModule::class,
    UIModuleBinds::class
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
}