package io.golos.cyber_android.application.dependency_injection.graph.app.ui.editor_page_fragment

import dagger.Subcomponent
import io.golos.cyber_android.ui.shared_fragments.editor.view.EditorPageFragment
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