package io.golos.cyber_android.ui.screens.app_start.sign_up.select_method.di

import dagger.Binds
import dagger.Module
import io.golos.cyber_android.ui.screens.app_start.sign_up.select_method.social_network_auth_providers.FacebookAuthProvider
import io.golos.cyber_android.ui.screens.app_start.sign_up.select_method.social_network_auth_providers.GoogleAuthProvider
import io.golos.cyber_android.ui.screens.app_start.sign_up.select_method.social_network_auth_providers.SocialNetworkAuthProvider
import io.golos.domain.dependency_injection.Clarification
import javax.inject.Named

@Module
abstract class SignUpSelectMethodFragmentModuleBinds {
    @Binds
    @Named(Clarification.GOOGLE)
    abstract fun provideGoogleAuth(auth: GoogleAuthProvider): SocialNetworkAuthProvider

    @Binds
    @Named(Clarification.FACEBOOK)
    abstract fun provideFacebookAuth(auth: FacebookAuthProvider): SocialNetworkAuthProvider
}