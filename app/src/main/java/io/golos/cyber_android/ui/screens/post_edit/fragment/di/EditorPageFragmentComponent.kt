package io.golos.cyber_android.ui.screens.post_edit.fragment.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.post_edit.fragment.view.EditorPageFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    EditorPageFragmentModule::class,
    EditorPageFragmentModuleBinds::class
])
@FragmentScope
interface EditorPageFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: EditorPageFragmentModule): Builder
        fun build(): EditorPageFragmentComponent
    }

    fun inject(fragment: EditorPageFragment)
}