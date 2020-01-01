package io.golos.cyber_android.ui.shared.widgets.sorting

import androidx.annotation.StringRes
import io.golos.cyber_android.R

/**
 * Base interface for sorting/filtering in [SortingWidget]
 */
interface SortingType {
    /**
     * String res of the title of this sort/filter
     */
    val title: Int
}

enum class TrendingSort(@StringRes override val title: Int): SortingType {
    NEW(R.string.sort_new),
    TOP(R.string.sort_top)
}

enum class TimeFilter(@StringRes override val title: Int): SortingType {
    PAST_24_HR(R.string.filter_time_24hr),
    PAST_WEEK(R.string.filter_time_week),
    PAST_MONTH(R.string.filter_time_month),
    PAST_YEAR(R.string.filter_time_year),
    OF_ALL_TIME(R.string.filter_time_of_all_time),
}