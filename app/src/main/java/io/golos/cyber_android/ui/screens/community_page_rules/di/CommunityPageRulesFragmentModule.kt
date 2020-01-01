package io.golos.cyber_android.ui.screens.community_page_rules.di

import dagger.Module
import dagger.Provides

@Module
class CommunityPageRulesFragmentModule constructor(private val rules: String) {

    @Provides
    internal fun provideRules(): String? = rules
}