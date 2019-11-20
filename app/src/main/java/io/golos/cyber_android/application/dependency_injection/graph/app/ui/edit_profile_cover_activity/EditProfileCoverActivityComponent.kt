package io.golos.cyber_android.application.dependency_injection.graph.app.ui.edit_profile_cover_activity

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.profile.old_profile.edit.cover.EditProfileCoverActivity
import io.golos.cyber_android.ui.screens.profile.old_profile.edit.cover.EditProfileCoverFragment
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Subcomponent(modules = [
    EditProfileCoverActivityModule::class,
    EditProfileCoverActivityModuleBinds::class
])
@ActivityScope
interface EditProfileCoverActivityComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: EditProfileCoverActivityModule): Builder
        fun build(): EditProfileCoverActivityComponent
    }

    fun inject(activity: EditProfileCoverActivity)
    fun inject(fragment: EditProfileCoverFragment)
}