package io.golos.cyber_android.ui.screens.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.posts.PostsAdapter
import io.golos.cyber_android.ui.screens.feed.HeadersPostsAdapter.EditorWidgetViewHolder
import io.golos.cyber_android.ui.screens.feed.HeadersPostsAdapter.SortingWidgetViewHolder
import io.golos.cyber_android.widgets.EditorWidget
import io.golos.cyber_android.widgets.sorting.SortingWidget
import io.golos.cyber_android.widgets.sorting.TimeFilter
import io.golos.cyber_android.widgets.sorting.TrendingSort
import kotlinx.android.synthetic.main.item_loading.view.*

/**
 * Extension of [PostsAdapter] that support two types of headers -
 * [EditorWidgetViewHolder] and [SortingWidgetViewHolder]
 */
class HeadersPostsAdapter(
    listener: Listener,
    private val isEditorWidgetSupported: Boolean,
    var isSortingWidgetSupported: Boolean
) :
    PostsAdapter(emptyList(), listener) {

    private val EDITOR_TYPE = 0
    private val SORTING_TYPE = 1
    private val POST_TYPE = 2
    private val LOADING_TYPE = 3

    var isLoading = true
        set(value) {
            field = value
            notifyItemChanged(itemCount - 1)
        }

    var sortingWidgetState = SortingWidget.SortingWidgetState(TrendingSort.TOP, TimeFilter.PAST_24_HR)
        set(value) {
            checkSortingWidgetSupport()
            field = value
            notifyItemChanged(1)
        }

    var editorWidgetState = EditorWidget.EditorWidgetState(null)
        set(value) {
            checkEditorWidgetSupport()
            field = value
            notifyItemChanged(0)
        }

    var sortingWidgetListener: SortingWidget.Listener? = null
        set(value) {
            checkSortingWidgetSupport()
            field = value
        }

    var editorWidgetListener: EditorWidget.Listener? = null
        set(value) {
            checkEditorWidgetSupport()
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            EDITOR_TYPE -> EditorWidgetViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_editor_widget, parent,
                    false
                ) as EditorWidget
            )
            SORTING_TYPE -> SortingWidgetViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_sorting_widget,
                    parent,
                    false
                ) as SortingWidget
            )
            LOADING_TYPE -> LoadingViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_loading,
                    parent,
                    false
                )
            )
            POST_TYPE -> super.onCreateViewHolder(parent, viewType)
            else -> throw RuntimeException("Unsupported view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            POST_TYPE -> {
                super.onBindViewHolder(holder, position - getItemsOffset())
            }
            EDITOR_TYPE -> {
                holder as EditorWidgetViewHolder
                holder.bind(editorWidgetState, editorWidgetListener)
            }
            SORTING_TYPE -> {
                holder as SortingWidgetViewHolder
                holder.bind(sortingWidgetState, sortingWidgetListener)
            }
            LOADING_TYPE -> {
                holder as LoadingViewHolder
                holder.bind(isLoading)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        if (isEditorWidgetSupported) {
            if (position == 0) return EDITOR_TYPE
            if (isSortingWidgetSupported && position == 1) return SORTING_TYPE
        }
        if (position == itemCount - 1) return LOADING_TYPE
        return POST_TYPE
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + getItemsOffset() + 1
    }

    private val adapterCallback = AdapterListUpdateCallback(this)
    private val updateCallback = object : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapterCallback.onChanged(position + getItemsOffset(), count, payload)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapterCallback.onMoved(fromPosition + getItemsOffset(), toPosition + getItemsOffset())
        }

        override fun onInserted(position: Int, count: Int) {
            adapterCallback.onInserted(position + getItemsOffset(), count)
        }

        override fun onRemoved(position: Int, count: Int) {
            adapterCallback.onRemoved(position + getItemsOffset(), count)
        }
    }

    override fun dispatchUpdates(diffResult: DiffUtil.DiffResult) {
        diffResult.dispatchUpdatesTo(updateCallback)
    }

    /**
     * Return headers count to offset real content elements
     */
    private fun getItemsOffset() = if (isEditorWidgetSupported) 1 else 0 +
            if (isSortingWidgetSupported) 1 else 0

    private fun checkEditorWidgetSupport() {
        if (!isEditorWidgetSupported) {
            throw RuntimeException("Editor widget not supported")
        }
    }

    private fun checkSortingWidgetSupport() {
        if (!isSortingWidgetSupported) {
            throw RuntimeException("Sorting widget not supported")
        }
    }

    /**
     * [RecyclerView.ViewHolder] for [EditorWidget]
     */
    class EditorWidgetViewHolder(val view: EditorWidget) : RecyclerView.ViewHolder(view) {
        fun bind(
            editorWidgetState: EditorWidget.EditorWidgetState,
            editorWidgetListener: EditorWidget.Listener?
        ) {
            view.listener = editorWidgetListener
            view.loadUserAvatar(editorWidgetState.avatarUrl ?: "")
        }
    }

    /**
     * [RecyclerView.ViewHolder] for indicating loading process
     */
    class LoadingViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(
            isLoading: Boolean
        ) {
            view.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    /**
     * [RecyclerView.ViewHolder] for [SortingWidget]
     */
    class SortingWidgetViewHolder(val view: SortingWidget) : RecyclerView.ViewHolder(view) {
        fun bind(
            sortingWidgetState: SortingWidget.SortingWidgetState,
            sortingWidgetListener: SortingWidget.Listener?
        ) {
            view.setTrendingSort(sortingWidgetState.sort)
            view.setTimeFilter(sortingWidgetState.filter)
            view.listener = sortingWidgetListener
        }
    }
}