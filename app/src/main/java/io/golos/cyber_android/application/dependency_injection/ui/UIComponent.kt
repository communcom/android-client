package io.golos.cyber_android.application.dependency_injection.ui

import dagger.Subcomponent
import io.golos.domain.dependency_injection.scopes.UIScope

@Subcomponent(modules = [
    UIModule::class,
    UIModuleBinds::class
])
@UIScope
interface UIComponent {
}