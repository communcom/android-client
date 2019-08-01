package io.golos.cyber_android.ui.screens.profile.edit.settings.notifications

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.golos.cyber_android.R
import io.golos.domain.entities.NotificationSettingsEntity
import io.golos.domain.requestmodel.NotificationSettingsModel


data class NotificationSetting(@DrawableRes val icon: Int, @StringRes val title: Int, var isEnabled: Boolean = true)

//todo add descriptions
internal fun NotificationSettingsModel.toSettingsList(): List<NotificationSetting> {
    return listOf(
        NotificationSetting(
            R.drawable.ic_settings_upvote,
            R.string.upvote,
            this.showUpvote
        ),
        NotificationSetting(
            R.drawable.ic_settings_downvote,
            R.string.downvote,
            this.showDownvote
        ),
        NotificationSetting(
            R.drawable.ic_settings_comment,
            R.string.comment_and_reply,
            this.showReply
        ),
        NotificationSetting(
            R.drawable.ic_settings_points_transfer,
            R.string.points_transfer,
            this.showTransfer
        ),

        NotificationSetting(
            R.drawable.ic_settings_subscribe,
            R.string.subscription_to_your_blog,
            this.showSubscribe
        ),
        NotificationSetting(
            R.drawable.ic_settings_unsubscribe,
            R.string.unsubscription_from_your_blog,
            this.showUnsubscribe
        ),

        NotificationSetting(
            R.drawable.ic_settings_mention,
            R.string.mention,
            this.showMention
        ),
        NotificationSetting(
            R.drawable.ic_settings_repost,
            R.string.repost,
            this.showRepost
        ),

        NotificationSetting(
            R.drawable.ic_settings_delegate_vote,
            R.string.vote_for_you_delegate,
            this.showWitnessVote
        ),
        NotificationSetting(
            R.drawable.ic_settings_delegate_vote_cancel,
            R.string.vote_cancellation_delegate,
            this.showWitnessCancelVote
        ),

        NotificationSetting(
            R.drawable.ic_settings_rewards_for_posts,
            R.string.rewards_for_posts,
            this.showReward
        ),

        NotificationSetting(
            R.drawable.ic_settings_rewards_for_posts_1,
            R.string.curator_award,
            this.showCuratorReward
        )
    )
}

internal fun List<NotificationSetting>.toSettingsEntity(): NotificationSettingsEntity {
    return NotificationSettingsEntity(
        this[0].isEnabled,
        this[1].isEnabled,
        this[2].isEnabled,
        this[3].isEnabled,
        this[4].isEnabled,
        this[5].isEnabled,
        this[6].isEnabled,
        this[7].isEnabled,
        this[8].isEnabled,
        this[9].isEnabled,
        this[10].isEnabled,
        this[11].isEnabled
    )
}

