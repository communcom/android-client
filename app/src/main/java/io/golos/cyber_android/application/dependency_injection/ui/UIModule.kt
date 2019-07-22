package io.golos.cyber_android.application.dependency_injection.ui

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.BuildConfig
import io.golos.domain.interactors.model.TestPassProvider

@Module
class UIModule {
    @Provides
    internal fun provideTestPassProvider(): TestPassProvider = object: TestPassProvider {
        override fun provide() = BuildConfig.AUTH_TEST_PASS
    }
}