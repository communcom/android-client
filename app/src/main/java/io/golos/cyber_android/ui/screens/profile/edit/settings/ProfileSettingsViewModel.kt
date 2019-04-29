package io.golos.cyber_android.ui.screens.profile.edit.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.R

class ProfileSettingsViewModel : ViewModel() {
    private val settingsLiveData = MutableLiveData<List<NotificationSetting>>(
        listOf(
            NotificationSetting(R.drawable.ic_settings_upvote, R.string.upvote),
            NotificationSetting(R.drawable.ic_settings_downvote, R.string.downvote),
            NotificationSetting(R.drawable.ic_settings_points_transfer, R.string.points_transfer),
            NotificationSetting(R.drawable.ic_settings_comment, R.string.comment_and_reply),
            NotificationSetting(R.drawable.ic_settings_mention, R.string.mention),
            NotificationSetting(R.drawable.ic_settings_rewards_for_posts, R.string.rewards_for_posts),
            NotificationSetting(R.drawable.ic_settings_rewards_for_posts_1, R.string.rewards_for_posts),
            NotificationSetting(R.drawable.ic_settings_following, R.string.following),
            NotificationSetting(R.drawable.ic_settings_repost, R.string.repost)
        )
    )

    var getSettingsLiveData = settingsLiveData as LiveData<List<NotificationSetting>>


    fun onNotificationSettingChanged(item: NotificationSetting, isEnabled: Boolean) {
        item.isEnabled = isEnabled
    }


}
