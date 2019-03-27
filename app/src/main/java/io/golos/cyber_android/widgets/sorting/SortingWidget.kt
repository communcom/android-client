package io.golos.cyber_android.widgets.sorting

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.view_sorting_widget.view.*

/**
 * Custom view which represents Sorting Widget. With this widget user can change the way of sorting and filtering the feed -
 * sorting by new/top posts and filtering by time. Those sorts/filters represented by [TrendingSort] and [TimeFilter] enums.
 */
class SortingWidget : LinearLayout {

    var listener: Listener? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        inflate(context, R.layout.view_sorting_widget, this)

        sorting.setOnClickListener {
            listener?.onTrendingSortClick()
        }

        timeFiltering.setOnClickListener {
            listener?.onTimeFilterClick()
        }

        val arrow = VectorDrawableCompat.create(resources, R.drawable.ic_sorting_arrow, null)
        sorting.setCompoundDrawablesWithIntrinsicBounds(null, null, arrow, null)
        timeFiltering.setCompoundDrawablesWithIntrinsicBounds(null, null, arrow, null)
    }

    fun setTrendingSort(sort: TrendingSort) {
        sorting.setText(sort.title)
    }

    fun setTimeFilter(filter: TimeFilter) {
        timeFiltering.setText(filter.title)
    }

    interface Listener {
        fun onTrendingSortClick()
        fun onTimeFilterClick()
    }

    /**
     * State of the sorting widget
     */
    data class SortingWidgetState(var sort: TrendingSort, var filter: TimeFilter)
}