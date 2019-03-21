package io.golos.cyber_android.ui.screens.feed

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.recyclerview.widget.*
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.posts.PostsAdapter
import io.golos.cyber_android.ui.screens.feed.HeadersPostsAdapter.EditorWidgetViewHolder
import io.golos.cyber_android.ui.screens.feed.HeadersPostsAdapter.SortingWidgetViewHolder
import io.golos.cyber_android.widgets.EditorWidget
import io.golos.cyber_android.widgets.sorting.SortingWidget
import io.golos.cyber_android.widgets.sorting.TimeFilter
import io.golos.cyber_android.widgets.sorting.TrendingSort
import io.golos.domain.interactors.model.PostModel

/**
 * Extension of [PostsAdapter] that support two types of headers -
 * [EditorWidgetViewHolder] and [SortingWidgetViewHolder]
 */
internal class HeadersPostsAdapter(
    diffCallback: DiffUtil.ItemCallback<PostModel>,
    listener: Listener,
    private val supportEditorWidget: Boolean,
    var supportSortingWidget: Boolean
) :
    PostsAdapter(diffCallback, listener) {

    private val EDITOR_TYPE = 0
    private val SORTING_TYPE = 1
    private val POST_TYPE = 2

    val sortingWidgetState = SortingWidgetState(TrendingSort.TOP, TimeFilter.PAST_24_HR)
    val editorWidgetState = EditorWidgetState(null)

    var sortingWidgetListener: SortingWidget.Listener? = null
        set(value) {
            if (!supportSortingWidget) {
                throw RuntimeException("Sorting widget not supported")
            }
            field = value
        }

    var editorWidgetListener: EditorWidget.Listener? = null
        set(value) {
            if (!supportSortingWidget) {
                throw RuntimeException("Editor widget not supported")
            }
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            EDITOR_TYPE -> EditorWidgetViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_editor_widget, parent, false) as EditorWidget
            )
            SORTING_TYPE -> SortingWidgetViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_sorting_widget,
                    parent,
                    false
                ) as SortingWidget
            )
            POST_TYPE -> super.onCreateViewHolder(parent, viewType)
            else -> throw RuntimeException("Unsupported view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            POST_TYPE -> {
                super.onBindViewHolder(holder, position)
            }
            EDITOR_TYPE -> {
                holder as EditorWidgetViewHolder
                holder.bind(editorWidgetState, editorWidgetListener)
            }
            SORTING_TYPE -> {
                holder as SortingWidgetViewHolder
                holder.bind(sortingWidgetState, sortingWidgetListener)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        if (supportEditorWidget) {
            if (position == 0) return EDITOR_TYPE
            if (supportSortingWidget)
                if (position == 1) return SORTING_TYPE
        }
        return POST_TYPE
    }

    override fun getItemCount(): Int {
        return differ.itemCount + getItemsOffset()
    }

    /**
     * Return headers count to offset real content elements
     */
    private fun getItemsOffset() = if (supportEditorWidget) 1 else 0 +
            if (supportSortingWidget) 1 else 0

    private val adapterCallback = AdapterListUpdateCallback(this)

    private val listUpdateCallback = object : ListUpdateCallback {
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

    private val differ = AsyncPagedListDiffer<PostModel>(
        listUpdateCallback,
        AsyncDifferConfig.Builder<PostModel>(diffCallback).build()
    )

    override fun getItem(position: Int): PostModel? {
        return differ.getItem(position - getItemsOffset())
    }

    override fun submitList(pagedList: PagedList<PostModel>?) {
        differ.submitList(pagedList)
    }

    override fun getCurrentList(): PagedList<PostModel>? {
        return differ.currentList
    }

    fun setTrendingSort(sort: TrendingSort) {
        sortingWidgetState.sort = sort
        notifyItemChanged(1)
    }

    fun setTimeFilter(filter: TimeFilter) {
        sortingWidgetState.filter = filter
        notifyItemChanged(1)
    }

    fun setAvatarUrl(url: String?) {
        editorWidgetState.avatarUrl = url
        notifyItemChanged(0)
    }

    /**
     * [RecyclerView.ViewHolder] for [EditorWidget]
     */
    class EditorWidgetViewHolder(val view: EditorWidget) : RecyclerView.ViewHolder(view) {
        fun bind(
            editorWidgetState: EditorWidgetState,
            editorWidgetListener: EditorWidget.Listener?
        ) {
            view.listener = editorWidgetListener
        }
    }

    /**
     * [RecyclerView.ViewHolder] for [SortingWidget]
     */
    class SortingWidgetViewHolder(val view: SortingWidget) : RecyclerView.ViewHolder(view) {
        fun bind(
            sortingWidgetState: SortingWidgetState,
            sortingWidgetListener: SortingWidget.Listener?
        ) {
            view.setTrendingSort(sortingWidgetState.sort)
            view.setTimeFilter(sortingWidgetState.filter)
            view.listener = sortingWidgetListener
        }
    }

    /**
     * State of the sorting widget. Can be written to and restored from parcel
     */
    class SortingWidgetState(var sort: TrendingSort, var filter: TimeFilter) : Parcelable {
        constructor(source: Parcel) : this(
            TrendingSort.valueOf(source.readString() ?: ""),
            TimeFilter.valueOf(source.readString() ?: "")
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            dest.writeString(sort.name)
            dest.writeString(filter.name)
        }

        @Suppress("unused")
        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SortingWidgetState> = object : Parcelable.Creator<SortingWidgetState> {
                override fun createFromParcel(source: Parcel): SortingWidgetState = SortingWidgetState(source)
                override fun newArray(size: Int): Array<SortingWidgetState?> = arrayOfNulls(size)
            }
        }
    }

    /**
     * State of the editor widget. Can be written to and restored from parcel
     */
    class EditorWidgetState(var avatarUrl: String?) : Parcelable {

        constructor(source: Parcel) : this(source.readString())

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            dest.writeString(avatarUrl)
        }

        @Suppress("unused")
        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<EditorWidgetState> = object : Parcelable.Creator<EditorWidgetState> {
                override fun createFromParcel(source: Parcel): EditorWidgetState = EditorWidgetState(source)
                override fun newArray(size: Int): Array<EditorWidgetState?> = arrayOfNulls(size)
            }
        }
    }
}