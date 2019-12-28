package io.golos.cyber_android.ui.screens.community_page.child_pages.community_post.model

import io.golos.cyber_android.ui.screens.post_filters.PostFiltersHolder

data class TimeConfigurationDomain(
    val timeFilter: PostFiltersHolder.UpdateTimeFilter,
    val periodFilter: PostFiltersHolder.PeriodTimeFilter
)