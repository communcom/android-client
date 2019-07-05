package io.golos.cyber_android.ui.screens.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.LoadingViewHolder
import io.golos.cyber_android.ui.screens.notifications.NotificationsAdapter.NotificationViewHolder
import io.golos.cyber_android.utils.DateUtils
import io.golos.domain.interactors.model.ElapsedTime
import io.golos.domain.requestmodel.*
import kotlinx.android.synthetic.main.item_notification.view.*

private const val ITEM_TYPE = 0
private const val LOADING_TYPE = 1


/**
 * [RecyclerView.Adapter] for [EventModel] objects. Most of the view contains timestamp, message (see [EventMessagesUtils]),
 * icon and avatar.
 * For additional information see [NotificationViewHolder.bindEvent] methods.
 */
class NotificationsAdapter(private val listener: (EventModel) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var values: List<EventModel> = emptyList()

    var isLoading = true
        set(value) {
            field = value
            if (itemCount > 0)
                notifyItemChanged(getLoadingViewHolderPosition())
        }

    fun submit(list: List<EventModel>) {
        val diff = DiffUtil.calculateDiff(EventDiffCallback(values, list))
        values = list
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE -> NotificationViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_notification, parent, false
                )
            )
            LOADING_TYPE -> LoadingViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_loading,
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException("Unsupported view type")
        }
    }


    override fun getItemCount() = values.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_TYPE -> (holder as NotificationViewHolder).bind(values[position], listener)
            LOADING_TYPE -> (holder as LoadingViewHolder).bind(isLoading)
        }
    }

    override fun getItemViewType(position: Int) =
        if (position == getLoadingViewHolderPosition()) LOADING_TYPE else ITEM_TYPE

    /**
     * Returns position of loading view holder
     */
    private fun getLoadingViewHolderPosition() = itemCount - 1

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        /**
         * Binds [EventModel] to this [NotificationViewHolder]. This method only passes item to corresponding
         * [bindEvent] method
         */
        fun bind(item: EventModel, listener: (EventModel) -> Unit) {
            itemView.root.setOnClickListener { listener(item) }
            when (item) {
                is VoteEventModel -> bindEvent(item)
                is FlagEventModel -> bindEvent(item)
                is TransferEventModel -> bindEvent(item)
                is SubscribeEventModel -> bindEvent(item)
                is UnSubscribeEventModel -> bindEvent(item)
                is ReplyEventModel -> bindEvent(item)
                is MentionEventModel -> bindEvent(item)
                is RepostEventModel -> bindItem(item)
                is AwardEventModel -> skip(item)
                is CuratorAwardEventModel -> bindEvent(item)
                is WitnessVoteEventModel -> skip(item)
                is WitnessCancelVoteEventModel -> skip(item)
            }
        }

        /**
         * Calling this method indicates that this [EventModel] wasn't handled due to missing design for this type of event.
         * TODO all of the event should be handled, this method should be removed in future
         */
        private fun skip(item: EventModel) {
            itemView.summary.text = "skiped " + item.javaClass.name
        }

        private fun bindEvent(item: SubscribeEventModel) {
            with(itemView) {
                loadUserAvatar(item.actor.avatarUrl, item.actor.id.name)
                setTimestamp(item.timestamp.time, item.elapsedTime, item.community.name)
                summary.text = item.getMessage(itemView.context)
                icon.setImageResource(R.drawable.ic_notifications_follow)
                icon.setBackgroundResource(R.drawable.bg_notification_icon_transfer)
                icon.visibility = View.VISIBLE
            }
        }

        private fun bindEvent(item: UnSubscribeEventModel) {
            with(itemView) {
                loadUserAvatar(item.actor.avatarUrl, item.actor.id.name)
                setTimestamp(item.timestamp.time, item.elapsedTime, item.community.name)
                summary.text = item.getMessage(itemView.context)
                icon.setImageResource(R.drawable.ic_notifications_follow)
                icon.setBackgroundResource(R.drawable.bg_notification_icon_transfer)
                icon.visibility = View.VISIBLE
            }
        }

        private fun bindEvent(item: MentionEventModel) {
            with(itemView) {
                loadUserAvatar(item.actor.avatarUrl, item.actor.id.name)
                setTimestamp(item.timestamp.time, item.elapsedTime, item.community.name)
                summary.text = item.getMessage(itemView.context)
                icon.setImageResource(R.drawable.ic_notifications)
                icon.setBackgroundResource(R.drawable.bg_notification_icon_mention)
                icon.visibility = View.VISIBLE
            }
        }

        private fun bindItem(item: RepostEventModel) {
            with(itemView) {
                loadUserAvatar(item.actor.avatarUrl, item.actor.id.name)
                setTimestamp(item.timestamp.time, item.elapsedTime, item.community.name)
                summary.text = item.getMessage(itemView.context)
                icon.setImageResource(R.drawable.ic_send)
                icon.setBackgroundResource(R.drawable.bg_notification_icon_transfer)
                icon.visibility = View.VISIBLE
            }
        }

        private fun bindEvent(item: CuratorAwardEventModel) {
            with(itemView) {
                avatar.setImageResource(R.drawable.ic_notifications_rewards_posts)
                avatar.setBackgroundResource(R.drawable.bg_notifications_awards)
                setTimestamp(item.timestamp.time, item.elapsedTime, item.community.name)
                summary.text = item.getMessage(itemView.context)
                icon.visibility = View.GONE
            }
        }

        private fun bindEvent(item: ReplyEventModel) {
            with(itemView) {
                loadUserAvatar(item.actor.avatarUrl, item.actor.id.name)
                setTimestamp(item.timestamp.time, item.elapsedTime, item.community.name)
                summary.text = item.getMessage(itemView.context)
                icon.setImageResource(R.drawable.ic_notifications_comment)
                icon.setBackgroundResource(R.drawable.bg_notification_icon_transfer)
                icon.visibility = View.VISIBLE
            }
        }

        private fun bindEvent(item: VoteEventModel) {
            with(itemView) {
                loadUserAvatar(item.actor.avatarUrl, item.actor.id.name)
                setTimestamp(item.timestamp.time, item.elapsedTime, item.actor.id.name)
                summary.text = item.getMessage(itemView.context)
                icon.setImageResource(R.drawable.ic_upvote)
                icon.setBackgroundResource(R.drawable.bg_notification_icon_upvote)
                icon.visibility = View.VISIBLE
            }
        }

        private fun bindEvent(item: FlagEventModel) {
            with(itemView) {
                loadUserAvatar(item.actor.avatarUrl, item.actor.id.name)
                setTimestamp(item.timestamp.time, item.elapsedTime, item.actor.id.name)
                summary.text = item.getMessage(itemView.context)
                icon.setImageResource(R.drawable.ic_downvote)
                icon.setBackgroundResource(R.drawable.bg_notification_icon_downvote)
                icon.visibility = View.VISIBLE
            }
        }

        private fun bindEvent(item: TransferEventModel) {
            with(itemView) {
                loadUserAvatar(item.actor.avatarUrl, item.actor.id.name)
                setTimestamp(item.timestamp.time, item.elapsedTime)
                summary.text = item.getMessage(itemView.context)
                icon.setImageResource(R.drawable.ic_notification_sent_points)
                icon.setBackgroundResource(R.drawable.bg_notification_icon_transfer)
                icon.visibility = View.VISIBLE
            }
        }

        /**
         * Creates timestamp for this ViewHolder and appends actor name to it.
         * @param time time of the event
         * @param elapsedTime [ElapsedTime] passed after event. This should be provided by model
         * @param actorName name of the actor which should be appended to timestamp
         */
        private fun View.setTimestamp(
            time: Long,
            elapsedTime: ElapsedTime,
            actorName: String
        ) {
            timestamp.text = String.format(
                context.resources.getString(R.string.event_time_and_actor_format),
                DateUtils.createTimeLabel(
                    time,
                    elapsedTime.elapsedMinutes,
                    elapsedTime.elapsedHours,
                    elapsedTime.elapsedDays,
                    context
                ),
                actorName
            )
        }

        /**
         * Creates timestamp for this ViewHolder.
         * @param time time of the event
         * @param elapsedTime [ElapsedTime] passed after event. This should be provided by model
         */
        private fun View.setTimestamp(
            time: Long,
            elapsedTime: ElapsedTime
        ) {
            timestamp.text = DateUtils.createTimeLabel(
                time,
                elapsedTime.elapsedMinutes,
                elapsedTime.elapsedHours,
                elapsedTime.elapsedDays,
                context
            )
        }

        /**
         * Loads user avatar into this ViewHolder.
         * @param avatarUrl url of the avatar, nullable. If null, then default image will be loaded
         * @param username of the related user. Used to display default avatar when there is none
         */
        private fun View.loadUserAvatar(avatarUrl: String?, username: String) {
            if (!avatarUrl.isNullOrBlank()) {
                Glide.with(itemView.context).load(avatarUrl).apply(RequestOptions.circleCropTransform()).into(avatar)
                avatarName.text = ""
            } else {
                Glide.with(itemView.context).load(R.drawable.bg_profile_avatar_no_borders).into(avatar)
                avatarName.text = username
            }
        }
    }
}
