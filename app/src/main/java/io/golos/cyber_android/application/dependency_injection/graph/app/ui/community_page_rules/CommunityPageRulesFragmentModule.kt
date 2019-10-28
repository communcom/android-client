package io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page_rules

import dagger.Module
import dagger.Provides

@Module
class CommunityPageRulesFragmentModule constructor(private val rules: String) {

    @Provides
    internal fun provideRules(): String? = rules
}