package io.golos.cyber_android.application.dependency_injection.graph.app.ui

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.BuildConfig
import io.golos.domain.use_cases.model.TestPassProvider

@Module
class UIModule {
    @Provides
    internal fun provideTestPassProvider(): TestPassProvider = object: TestPassProvider {
        override fun provide() = BuildConfig.AUTH_TEST_PASS
    }
}