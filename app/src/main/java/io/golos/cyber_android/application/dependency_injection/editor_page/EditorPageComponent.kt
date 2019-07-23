package io.golos.cyber_android.application.dependency_injection.editor_page

import dagger.Subcomponent
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    EditorPageModule::class,
    EditorPageModuleBinds::class
])
@FragmentScope
interface EditorPageComponent {
}