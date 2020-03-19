package io.golos.cyber_android.ui.screens.login_sign_up_select_method.di

import dagger.Binds
import dagger.Module
import io.golos.cyber_android.ui.screens.login_sign_up_select_method.google.GoogleAuth
import io.golos.cyber_android.ui.screens.login_sign_up_select_method.google.GoogleAuthImpl

@Module
abstract class SignUpSelectMethodFragmentModuleBinds {
    @Binds
    abstract fun provideGoogleAuth(auth: GoogleAuthImpl): GoogleAuth
}