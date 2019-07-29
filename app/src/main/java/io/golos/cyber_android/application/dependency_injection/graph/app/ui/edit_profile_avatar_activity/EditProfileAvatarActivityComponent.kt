package io.golos.cyber_android.application.dependency_injection.graph.app.ui.edit_profile_avatar_activity

import dagger.Subcomponent
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Subcomponent(modules = [
    EditProfileAvatarActivityModule::class,
    EditProfileAvatarActivityModuleBinds::class
])
@ActivityScope
interface EditProfileAvatarActivityComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: EditProfileAvatarActivityModule): Builder
        fun build(): EditProfileAvatarActivityComponent
    }
}