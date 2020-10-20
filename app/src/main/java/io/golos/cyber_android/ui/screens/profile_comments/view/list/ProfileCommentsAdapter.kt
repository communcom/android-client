package io.golos.cyber_android.ui.screens.profile_comments.view.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.ui.screens.profile_comments.model.item.ProfileCommentErrorListItem
import io.golos.cyber_android.ui.screens.profile_comments.model.item.ProfileCommentListItem
import io.golos.cyber_android.ui.screens.profile_comments.model.item.ProfileCommentProgressListItem
import io.golos.cyber_android.ui.screens.profile_comments.view.item.ProfileCommentErrorItem
import io.golos.cyber_android.ui.screens.profile_comments.view.item.ProfileCommentItem
import io.golos.cyber_android.ui.screens.profile_comments.view.item.ProfileCommentProgressItem
import io.golos.cyber_android.ui.screens.profile_comments.view.list.ProfileCommentsViewType.PROFILE_VIEW_COMMENTS
import io.golos.cyber_android.ui.screens.profile_comments.view.list.ProfileCommentsViewType.PROFILE_VIEW_ERROR
import io.golos.cyber_android.ui.screens.profile_comments.view.list.ProfileCommentsViewType.PROFILE_VIEW_PROGRESS
import io.golos.cyber_android.ui.screens.profile_comments.view_model.ProfileCommentsModelEventProcessor
import io.golos.cyber_android.ui.shared.recycler_view.DiffAlgBase
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

class ProfileCommentsAdapter(
    processor: ProfileCommentsModelEventProcessor
) : VersionedListAdapterBase<ProfileCommentsModelEventProcessor>(processor, null) {

    private val rvViewPool = RecyclerView.RecycledViewPool()

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderBase<ProfileCommentsModelEventProcessor, VersionedListItem> {
        return when (viewType) {
            PROFILE_VIEW_COMMENTS -> {
                ProfileCommentItem(
                    parent,
                    rvViewPool
                ) as ViewHolderBase<ProfileCommentsModelEventProcessor, VersionedListItem>
            }
            PROFILE_VIEW_PROGRESS -> {
                ProfileCommentProgressItem(
                    parent
                ) as ViewHolderBase<ProfileCommentsModelEventProcessor, VersionedListItem>
            }
            PROFILE_VIEW_ERROR -> {
                ProfileCommentErrorItem(
                    parent
                ) as ViewHolderBase<ProfileCommentsModelEventProcessor, VersionedListItem>
            }
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is ProfileCommentListItem -> PROFILE_VIEW_COMMENTS
        is ProfileCommentProgressListItem -> PROFILE_VIEW_PROGRESS
        is ProfileCommentErrorListItem -> PROFILE_VIEW_ERROR
        else -> throw UnsupportedOperationException("This type of item is not supported")
    }

    override fun createDiffAlg(
        oldData: List<VersionedListItem>,
        newData: List<VersionedListItem>
    ): DiffAlgBase<VersionedListItem> {
        return object : DiffAlgBase<VersionedListItem>(oldData, newData) {

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }
        }
    }

    fun addProgress() {
        val item = items.find { it is ProfileCommentProgressListItem }
        if (item == null) {
            val adapterItemsList: ArrayList<VersionedListItem> = ArrayList(items)
            adapterItemsList.add(ProfileCommentProgressListItem())
            update(adapterItemsList)
        }
    }

    fun removeProgress() {
        val item = items.find { it is ProfileCommentProgressListItem }
        if (item != null) {
            val adapterItemsList: ArrayList<VersionedListItem> = ArrayList(items)
            adapterItemsList.remove(item)
            update(adapterItemsList)
        }
    }

    fun addRetry() {
        val item = items.find { it is ProfileCommentErrorListItem }
        if (item == null) {
            val adapterItemsList: ArrayList<VersionedListItem> = ArrayList(items)
            adapterItemsList.add(ProfileCommentErrorListItem())
            update(adapterItemsList)
        }
    }

    fun removeRetry() {
        val item = items.find { it is ProfileCommentErrorListItem }
        if (item != null) {
            val adapterItemsList: ArrayList<VersionedListItem> = ArrayList(items)
            adapterItemsList.remove(item)
            update(adapterItemsList)
        }
    }
}