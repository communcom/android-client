package io.golos.cyber_android.ui.screens.post_filters

import io.golos.cyber_android.R
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

class PostFiltersHolder
@Inject constructor() {
    private val feedFiltersChannel: ConflatedBroadcastChannel<FeedFilters>

    private val openFeedTypeChannel: ConflatedBroadcastChannel<CurrentOpenTypeFeed>

    private var myFeedFilters = FeedFilters(UpdateTimeFilter.NEW, PeriodTimeFilter.ALL)

    private var trendingFilters = FeedFilters(UpdateTimeFilter.POPULAR, PeriodTimeFilter.PAST_24_HOURS)

    init {
        feedFiltersChannel = ConflatedBroadcastChannel(myFeedFilters)
        openFeedTypeChannel = ConflatedBroadcastChannel(CurrentOpenTypeFeed.MY_FEED)
    }

    suspend fun updateFeedFilters(filters: FeedFilters) {
        if (openFeedTypeChannel.value == CurrentOpenTypeFeed.MY_FEED) {
            myFeedFilters = filters
        } else {
            trendingFilters = filters
        }
        feedFiltersChannel.send(filters)
    }

    suspend fun updateOpenFeedType(type: CurrentOpenTypeFeed) {
        val filters: FeedFilters = if (type == CurrentOpenTypeFeed.MY_FEED) {
            myFeedFilters
        } else {
            trendingFilters
        }
        openFeedTypeChannel.send(type)
        feedFiltersChannel.send(filters)
    }

    val feedFiltersFlow: Flow<FeedFilters> = feedFiltersChannel.asFlow()

    val openTypeFeedFlow: Flow<CurrentOpenTypeFeed> = openFeedTypeChannel.asFlow()

    data class FeedFilters(
        val updateTimeFilter: UpdateTimeFilter,
        val periodTimeFilter: PeriodTimeFilter
    )

    enum class UpdateTimeFilter(val value: Int) {
        HOT(R.string.sort_hot), //TOP_REWARDS
        NEW(R.string.sort_new), //NEW
        POPULAR(R.string.sort_popular) //TOP_LIKES
    }

    enum class PeriodTimeFilter(val value: Int) {
        PAST_24_HOURS(R.string.filter_time_24hr), //DAY
        PAST_WEEK(R.string.filter_time_week), //WEEK
        PAST_MONTH(R.string.filter_time_month), //MONTH
        ALL(R.string.filter_time_all) //ALL
    }

    enum class CurrentOpenTypeFeed {
        MY_FEED,
        TRENDING
    }
}