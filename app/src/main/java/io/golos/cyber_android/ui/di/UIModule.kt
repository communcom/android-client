package io.golos.cyber_android.ui.di

import dagger.Module
import dagger.Provides
import io.golos.domain.use_cases.model.TestPassProvider

@Module
class UIModule {
    @Provides
    internal fun provideTestPassProvider(): TestPassProvider = object: TestPassProvider {
        override fun provide() = ""
    }
}
