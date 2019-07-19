package io.golos.cyber_android.application.dependency_injection

import dagger.Binds
import dagger.Module
import io.golos.cyber_android.ui.screens.login.signin.user_name.keys_extractor.MasterPassKeysExtractor
import io.golos.cyber_android.ui.screens.login.signin.user_name.keys_extractor.MasterPassKeysExtractorImpl

@Module
abstract class UIModuleBinds {
    // Sign In
    @Binds
    abstract fun provideMasterPassKeysExtractor(extractor: MasterPassKeysExtractorImpl): MasterPassKeysExtractor
}