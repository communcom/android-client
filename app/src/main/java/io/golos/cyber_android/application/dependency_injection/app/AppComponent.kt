package io.golos.cyber_android.application.dependency_injection.app

import io.golos.domain.dependency_injection.scopes.ApplicationScope
import dagger.Component

@Component(modules = [
    AppModule::class,
    AppModuleBinds::class,
    AppModuleChilds::class
])
@ApplicationScope
interface AppComponent {

/*
    val ui: UIComponent.Builder

    fun inject(app: App)

    fun inject(converter: MoneyTypeConverter)

    fun inject(worker: UpdateCurrencyRatesWorker)
*/
}