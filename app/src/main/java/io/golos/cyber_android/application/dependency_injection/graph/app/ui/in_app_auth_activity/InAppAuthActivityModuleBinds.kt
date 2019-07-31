package io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity

import dagger.Binds
import dagger.Module
import io.golos.cyber_android.ui.screens.in_app_auth_activity.navigation.Navigator
import io.golos.cyber_android.ui.screens.in_app_auth_activity.navigation.NavigatorImpl

@Module
abstract class InAppAuthActivityModuleBinds {
    @Binds
    abstract fun provideNavigator(navigator: NavigatorImpl): Navigator
}