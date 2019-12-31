package io.golos.cyber_android.application.dependency_injection.graph.app

import io.golos.domain.dependency_injection.scopes.ApplicationScope
import dagger.Component
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.UIComponent

@Component(modules = [
    AppModule::class,
    AppModuleBinds::class,
    AppModuleChilds::class
])
@ApplicationScope
interface AppComponent {
    val ui: UIComponent.Builder

    fun inject(app: App)
}