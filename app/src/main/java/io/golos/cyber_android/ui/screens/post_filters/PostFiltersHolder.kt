package io.golos.cyber_android.ui.screens.post_filters

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

class PostFiltersHolder @Inject constructor() {


    private val feedFiltersChannel: ConflatedBroadcastChannel<FeedFilters>



    suspend fun updateFeedFilters(filters: FeedFilters){
        feedFiltersChannel.send(filters)
    }

    init {
        val feedFilters = FeedFilters(UpdateTimeFilter.POPULAR, PeriodTimeFilter.PAST_24_HOURS)
        feedFiltersChannel = ConflatedBroadcastChannel(feedFilters)
    }

    val feedFiltersFlow: Flow<FeedFilters> = feedFiltersChannel.asFlow()

    data class FeedFilters(val updateTimeFilter: UpdateTimeFilter,
                           val periodTimeFilter: PeriodTimeFilter)

    enum class UpdateTimeFilter(val value: String) {
        HOT("Hot"), //TOP_REWARDS
        NEW("New"), //NEW
        POPULAR("Popular") //TOP_LIKES
    }

    enum class PeriodTimeFilter(val value: String) {
        PAST_24_HOURS("Past 24 hours"), //DAY
        PAST_WEEK("Past week"), //WEEK
        PAST_MONTH("Past month"), //MONTH
        ALL("All time") //ALL
    }
}