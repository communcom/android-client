package io.golos.cyber_android.ui.screens.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.LoadingViewHolder
import io.golos.cyber_android.utils.DateUtils
import io.golos.domain.interactors.model.ElapsedTime
import io.golos.domain.model.*
import kotlinx.android.synthetic.main.item_notification.view.*
import java.util.*

private const val ITEM_TYPE = 0
private const val LOADING_TYPE = 1


class NotificationsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
            ITEM_TYPE -> (holder as NotificationViewHolder).bind(values[position])
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
        fun bind(item: EventModel) {
            when (item) {
                is VoteEventModel -> bindEvent(item)
                is FlagEventModel -> bindEvent(item)
                is TransferEventModel -> bindEvent(item)
                is SubscribeEventModel -> skip(item)
                is UnSubscribeEventModel -> skip(item)
                is ReplyEventModel -> bindEvent(item)
                is MentionEventModel -> bindEvent(item)
                is RepostEventModel -> bindItem(item)
                is AwardEventModel -> skip(item)
                is CuratorAwardEventModel -> bindEvent(item)
                is MessageEventModel -> skip(item)
                is WitnessVoteEventModel -> skip(item)
                is WitnessCancelVoteEventModel -> skip(item)
            }
        }

        private fun bindEvent(item: MentionEventModel) {
            with(itemView) {
                loadUserAvatar(item.actor.avatarUrl)
                setTimestamp(item.timestamp.time, item.timestamp.asElapsedTime(), item.community.name)
                summary.text = item.getMessage(itemView.context)
                icon.setImageResource(R.drawable.ic_notifications)
                icon.setBackgroundResource(R.drawable.bg_notification_icon_mention)
                icon.visibility = View.VISIBLE
            }
        }

        private fun bindItem(item: RepostEventModel) {
            with(itemView) {
                loadUserAvatar(item.actor.avatarUrl)
                setTimestamp(item.timestamp.time, item.timestamp.asElapsedTime(), item.community.name)
                summary.text = item.getMessage(itemView.context)
                icon.setImageResource(R.drawable.ic_send)
                icon.setBackgroundResource(R.drawable.bg_notification_icon_transfer)
                icon.visibility = View.VISIBLE
            }
        }

        private fun skip(item: EventModel) {
            itemView.summary.text = "skiped " + item.javaClass.name
        }


        private fun bindEvent(item: CuratorAwardEventModel) {
            with(itemView) {
                avatar.setImageResource(R.drawable.ic_notifications_rewards_posts)
                avatar.setBackgroundResource(R.drawable.bg_notifications_awards)
                setTimestamp(item.timestamp.time, item.timestamp.asElapsedTime(), item.community.name)
                summary.text = item.getMessage(itemView.context)
                icon.visibility = View.GONE
            }
        }

        private fun bindEvent(item: ReplyEventModel) {
            with(itemView) {
                loadUserAvatar(item.actor.avatarUrl)
                setTimestamp(item.timestamp.time, item.timestamp.asElapsedTime(), item.community.name)
                summary.text = item.getMessage(itemView.context)
                icon.setImageResource(R.drawable.ic_notifications_comment)
                icon.setBackgroundResource(R.drawable.bg_notification_icon_transfer)
                icon.visibility = View.VISIBLE
            }
        }

        private fun bindEvent(item: VoteEventModel) {
            with(itemView) {
                loadUserAvatar(item.actor.avatarUrl)
                setTimestamp(item.timestamp.time, item.timestamp.asElapsedTime(), item.actor.id.name)
                summary.text = item.getMessage(itemView.context)
                icon.setImageResource(R.drawable.ic_upvote)
                icon.setBackgroundResource(R.drawable.bg_notification_icon_upvote)
                icon.visibility = View.VISIBLE
            }
        }

        private fun bindEvent(item: FlagEventModel) {
            with(itemView) {
                loadUserAvatar(item.actor.avatarUrl)
                setTimestamp(item.timestamp.time, item.timestamp.asElapsedTime(), item.actor.id.name)
                summary.text = item.getMessage(itemView.context)
                icon.setImageResource(R.drawable.ic_downvote)
                icon.setBackgroundResource(R.drawable.bg_notification_icon_downvote)
                icon.visibility = View.VISIBLE
            }
        }

        private fun bindEvent(item: TransferEventModel) {
            with(itemView) {
                loadUserAvatar(item.actor.avatarUrl)
                setTimestamp(item.timestamp.time, item.timestamp.asElapsedTime())
                summary.text = item.getMessage(itemView.context)
                icon.setImageResource(R.drawable.ic_notification_sent_points)
                icon.setBackgroundResource(R.drawable.bg_notification_icon_transfer)
                icon.visibility = View.VISIBLE
            }
        }

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

        private fun View.loadUserAvatar(avatarUrl: String?) {
            if (!avatarUrl.isNullOrBlank())
                Glide.with(itemView.context).load(avatarUrl).into(avatar)
            else Glide.with(itemView.context).load(R.drawable.img_example_avatar).into(avatar)
        }


        private fun Date.asElapsedTime(): ElapsedTime {
            //todo delete me
            val fromTimeStamp = this.time

            return fromTimeStamp.minutesElapsedFromTimeStamp().let { elapsedMinutesFromPostCreation ->
                val hoursElapsed = elapsedMinutesFromPostCreation / 60
                when {
                    elapsedMinutesFromPostCreation < 60 -> ElapsedTime(elapsedMinutesFromPostCreation, 0, 0)
                    hoursElapsed < 24 -> ElapsedTime(elapsedMinutesFromPostCreation, hoursElapsed, 0)
                    else -> {
                        val daysAgo = Math.round(hoursElapsed.toDouble() / 24)
                        ElapsedTime(elapsedMinutesFromPostCreation, hoursElapsed, daysAgo.toInt())
                    }
                }
            }
        }

        private fun Long.minutesElapsedFromTimeStamp(): Int {
            //todo delete me
            val currentTime = System.currentTimeMillis()
            val dif = currentTime - this
            val hour = 1000 * 60
            val hoursAgo = dif / hour
            return hoursAgo.toInt()
        }
    }
}
