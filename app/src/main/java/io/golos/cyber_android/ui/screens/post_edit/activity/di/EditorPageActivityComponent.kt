package io.golos.cyber_android.ui.screens.post_edit.activity.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.post_edit.activity.EditorPageActivity
import io.golos.cyber_android.ui.screens.post_edit.fragment.di.EditorPageFragmentComponent
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Subcomponent(modules = [
    EditorPageActivityModuleChilds::class,
    EditorPageActivityModuleBinds::class
])
@ActivityScope
interface EditorPageActivityComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): EditorPageActivityComponent
    }

    val editorPageFragment: EditorPageFragmentComponent.Builder

    fun inject(activity: EditorPageActivity)
}