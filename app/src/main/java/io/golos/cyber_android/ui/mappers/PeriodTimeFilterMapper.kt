package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.screens.post_filters.PostFiltersHolder
import io.golos.domain.dto.PostsConfigurationDomain

fun PostFiltersHolder.PeriodTimeFilter.mapToTimeFrameDomain(): PostsConfigurationDomain.TimeFrameDomain {
    return when (this) {
        PostFiltersHolder.PeriodTimeFilter.PAST_24_HOURS ->
            PostsConfigurationDomain.TimeFrameDomain.DAY
        PostFiltersHolder.PeriodTimeFilter.PAST_WEEK ->
            PostsConfigurationDomain.TimeFrameDomain.WEEK
        PostFiltersHolder.PeriodTimeFilter.PAST_MONTH ->
            PostsConfigurationDomain.TimeFrameDomain.MONTH
        PostFiltersHolder.PeriodTimeFilter.ALL ->
            PostsConfigurationDomain.TimeFrameDomain.ALL
    }
}