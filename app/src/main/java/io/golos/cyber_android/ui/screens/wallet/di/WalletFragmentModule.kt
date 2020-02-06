package io.golos.cyber_android.ui.screens.wallet.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import javax.inject.Named

@Module
class WalletFragmentModule(private val totalCommun: Double) {
    @Provides
    @Named(Clarification.TOTAL_COMMUN)
    fun provideTotalCommun(): Double = totalCommun
}