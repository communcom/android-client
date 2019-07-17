package io.golos.cyber_android.application.dependency_injection

import android.content.Context
import kotlin.reflect.KClass

/** Storage for Dagger components on application level  */
class DependencyInjectionStorage(private val appContext: Context) {

    private val components = mutableMapOf<KClass<*>, Any>()

    inline fun <reified T>get(vararg args: Any): T = getComponent(T::class, args)

    inline fun <reified T>release() = releaseComponent(T::class)

    @Suppress("UNCHECKED_CAST")
    fun <T>getComponent(type: KClass<*>, args: Array<out Any>): T {
        var result = components[type]
        if(result == null) {
            result = provideComponent<T>(type, args)
            components[type] = result!!
        }
        return result as T
    }

    fun releaseComponent(type: KClass<*>) = components.remove(type)

    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    private fun <T>provideComponent(type: KClass<*>, args: Array<out Any>): T {
        return when(type) {
/*
            AppComponent::class -> DaggerAppComponent.builder().appModule(AppModule(appContext)).build()

            UIComponent::class -> get<AppComponent>().ui.build()

            RootActivityComponent::class -> get<UIComponent>().rootActivity.build()
            SetupActivityComponent::class -> get<UIComponent>().setupActivity.build()
            LoginActivityComponent::class -> get<UIComponent>().loginActivity.build()

            MainActivityComponent::class -> get<UIComponent>().mainActivity.build()
            AccountsFragmentComponent::class -> get<MainActivityComponent>().accountsFragment.build()
            SettingsFragmentComponent::class -> get<MainActivityComponent>().settingsFragment.build()
            PaymentsFragmentComponent::class -> get<MainActivityComponent>().paymentsFragment.build()

            AddEditAccountActivityComponent::class -> get<UIComponent>().addEditAccountActivity.build()
            AddAccountFragmentComponent::class -> get<AddEditAccountActivityComponent>().addAccountsFragment.build()
            EditAccountFragmentComponent::class -> get<AddEditAccountActivityComponent>()
                .editAccountsFragment
                .init(EditAccountFragmentModule(args[0] as Long))
                .build()

            AddEditPaymentActivityComponent::class -> get<UIComponent>().addEditPaymentActivity.build()
            AddPaymentFragmentComponent::class -> get<AddEditPaymentActivityComponent>().addPaymentFragment.build()
*/

            else -> throw UnsupportedOperationException("This component is not supported: ${type.simpleName}")
        } as T
    }
}