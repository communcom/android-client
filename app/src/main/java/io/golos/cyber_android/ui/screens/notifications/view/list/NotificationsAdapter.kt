package io.golos.cyber_android.ui.screens.notifications.view.list

import android.view.ViewGroup
import androidx.annotation.IntDef
import io.golos.cyber_android.ui.screens.notifications.view.list.items.*
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.*
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.screens.profile_comments.model.item.ProfileCommentErrorListItem
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.*
import io.golos.domain.utils.IdUtil


class NotificationsAdapter (
    private val listEventsProcessor: NotificationsViewModelListEventsProcessor,
    pageSize:Int): VersionedListAdapterBase<NotificationsViewModelListEventsProcessor>(listEventsProcessor,pageSize) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        @NotificationHolderType viewType:  Int
    ): ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem> {
        return when(viewType){
            EMPTY_STUB -> NotificationEmptyStubViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            HEADER_DATE -> NotificationHeaderDateViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            MENTION -> NotificationMentionViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            REPLY -> NotificationMentionViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            SUBSCRIBE -> NotificationSubscribeViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            UP_VOTE -> NotificationUpVoteViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            PROGRESS -> LoadingListViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            ERROR -> RetryListViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            else -> throw UnsupportedOperationException("Undefined view holder")
        }
    }

    override fun getItemViewType(position: Int): @NotificationHolderType Int =
        when(items[position]) {
            is NotificationEmptyStubItem -> EMPTY_STUB
            is NotificationHeaderDateItem -> HEADER_DATE
            is NotificationMentionItem -> MENTION
            is NotificationReplyItem -> REPLY
            is NotificationSubscribeItem -> SUBSCRIBE
            is NotificationUpVoteItem -> UP_VOTE
            is LoadingListItem -> PROGRESS
            is ProfileCommentErrorListItem -> ERROR
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun onNextPageReached() {
        super.onNextPageReached()
        listEventsProcessor.loadMoreNotifications()
    }

    fun addProgress() {
        val item = items.find { it is LoadingListItem }
        if (item == null) {
            val adapterItemsList: ArrayList<VersionedListItem> = ArrayList(items)
            adapterItemsList.add(LoadingListItem(IdUtil.generateLongId(), 0))
            update(adapterItemsList)
        }
    }

    fun removeProgress() {
        val item = items.find { it is LoadingListItem }
        if (item != null) {
            val adapterItemsList: ArrayList<VersionedListItem> = ArrayList(items)
            adapterItemsList.remove(item)
            update(adapterItemsList)
        }
    }

    fun addRetry() {
        val item = items.find { it is RetryListItem }
        if (item == null) {
            val adapterItemsList: ArrayList<VersionedListItem> = ArrayList(items)
            adapterItemsList.add(RetryListItem(IdUtil.generateLongId(), 0))
            update(adapterItemsList)
        }
    }

    fun removeRetry() {
        val item = items.find { it is RetryListItem }
        if (item != null) {
            val adapterItemsList: ArrayList<VersionedListItem> = ArrayList(items)
            adapterItemsList.remove(item)
            update(adapterItemsList)
        }
    }

    private companion object {

        @Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
        @IntDef(EMPTY_STUB, HEADER_DATE, MENTION, REPLY, SUBSCRIBE, UP_VOTE, PROGRESS, ERROR)
        @Retention(AnnotationRetention.SOURCE)
        annotation class NotificationHolderType

        const val EMPTY_STUB = 0
        const val HEADER_DATE = 1
        const val MENTION = 2
        const val REPLY = 3
        const val SUBSCRIBE = 4
        const val UP_VOTE = 5
        const val PROGRESS = 6
        const val ERROR = 7
    }
}
