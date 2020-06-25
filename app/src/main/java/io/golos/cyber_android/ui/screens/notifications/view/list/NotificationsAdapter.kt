package io.golos.cyber_android.ui.screens.notifications.view.list

import android.view.ViewGroup
import androidx.annotation.IntDef
import io.golos.cyber_android.ui.screens.notifications.view.list.items.*
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.*
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.screens.profile_comments.model.item.ProfileCommentErrorListItem
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.*
import io.golos.utils.id.IdUtil

class NotificationsAdapter (
    private val listEventsProcessor: NotificationsViewModelListEventsProcessor,
    pageSize:Int): VersionedListAdapterBase<NotificationsViewModelListEventsProcessor>(listEventsProcessor,pageSize) {

    private companion object {
        @Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
        @IntDef(EMPTY_STUB, HEADER_DATE, MENTION, REPLY, SUBSCRIBE, UP_VOTE, PROGRESS, ERROR, TRANSFER)
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
        const val TRANSFER = 8
        const val REWARD = 9
        const val REFERRAL_REGISTRATION_BONUS = 10
        const val REFERRAL_PURCHASE_BONUS = 11
        const val UNSUPPORTED = 12
        const val DONATION = 13
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        @NotificationHolderType viewType:  Int
    ): ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem> {
        return when(viewType){
            EMPTY_STUB -> NotificationEmptyStubViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            HEADER_DATE -> NotificationHeaderDateViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            MENTION -> NotificationMentionViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            REPLY -> NotificationReplyViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            SUBSCRIBE -> NotificationSubscribeViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            UP_VOTE -> NotificationUpVoteViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            PROGRESS -> LoadingListViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            ERROR -> RetryListViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            TRANSFER -> NotificationTransferViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            REWARD -> NotificationRewardViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            REFERRAL_PURCHASE_BONUS -> NotificationReferralPurchaseBonusViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            REFERRAL_REGISTRATION_BONUS -> NotificationReferralRegistrationBonusViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            DONATION -> NotificationDonationViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            UNSUPPORTED -> NotificationUnsupportedViewHolder(parent) as ViewHolderBase<NotificationsViewModelListEventsProcessor, VersionedListItem>
            else -> throw UnsupportedOperationException("Undefined view holder")
        }
    }

    override fun getItemViewType(position: Int): @NotificationHolderType Int =
        when(items[position]) {
            is EmptyStubNotificationItem -> EMPTY_STUB
            is HeaderDateNotificationItem -> HEADER_DATE
            is MentionNotificationItem -> MENTION
            is ReplyNotificationItem -> REPLY
            is SubscribeNotificationItem -> SUBSCRIBE
            is UpVoteNotificationItem -> UP_VOTE
            is LoadingListItem -> PROGRESS
            is ProfileCommentErrorListItem -> ERROR
            is TransferNotificationItem -> TRANSFER
            is RewardNotificationItem -> REWARD
            is ReferralPurchaseBonusNotificationItem -> REFERRAL_PURCHASE_BONUS
            is ReferralRegistrationBonusNotificationItem -> REFERRAL_REGISTRATION_BONUS
            is DonationNotificationItem -> DONATION
            is RetryListItem -> ERROR
            is UnsupportedNotificationItem -> UNSUPPORTED
            else -> throw UnsupportedOperationException("This type of item is not supported: ${items[position]::class.simpleName}")
        }

    override fun onNextPageReached() {
        super.onNextPageReached()
        listEventsProcessor.loadMoreNotifications()
    }

    override fun checkNextPageReached(pageSize: Int?, itemsSize: Int, position: Int) {
        pageSize?.let {
            if (position  >= items.size-1 && items.last() !is RetryListItem ) {
                onNextPageReached()
            }
        }
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
}
