package io.golos.cyber_android.ui.screens.community_page_rules.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dto.CommunityRuleDomain

@Suppress("unused")
@Module
class CommunityPageRulesFragmentModule constructor(private val rules: List<CommunityRuleDomain>) {
    @Provides
    internal fun provideRules(): List<CommunityRuleDomain> = rules
}