package io.golos.cyber_android.ui.screens.post_filters

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

class PostFiltersHolder @Inject constructor() {


    private val feedFiltersChannel: ConflatedBroadcastChannel<FeedFilters>

    private val openFeedType: CurrentOpenTypeFeed = CurrentOpenTypeFeed.MY_FEED

    private var myFeedFilters = FeedFilters(UpdateTimeFilter.NEW, PeriodTimeFilter.ALL)

    private var trendingFilters = FeedFilters(UpdateTimeFilter.HOT, PeriodTimeFilter.ALL)

    init {

        feedFiltersChannel = ConflatedBroadcastChannel(myFeedFilters)
    }

    suspend fun updateFeedFilters(filters: FeedFilters){
        if(openFeedType == CurrentOpenTypeFeed.MY_FEED){
            myFeedFilters = filters
        } else{
            trendingFilters = filters
        }
        feedFiltersChannel.send(filters)
    }

    suspend fun updateOpenFeedType(type: CurrentOpenTypeFeed){
        val filters: FeedFilters
        if(openFeedType == CurrentOpenTypeFeed.MY_FEED){
            filters = myFeedFilters
        } else{
            filters = trendingFilters
        }
        feedFiltersChannel.send(filters)
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

    enum class CurrentOpenTypeFeed{
        MY_FEED,
        TRENDING
    }
}