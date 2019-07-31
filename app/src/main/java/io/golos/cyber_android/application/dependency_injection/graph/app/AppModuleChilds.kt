package io.golos.cyber_android.application.dependency_injection.graph.app

import dagger.Module
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.UIComponent

@Module(subcomponents = [
    UIComponent::class
])
class AppModuleChilds