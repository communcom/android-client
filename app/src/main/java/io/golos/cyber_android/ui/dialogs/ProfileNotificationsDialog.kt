package io.golos.cyber_android.ui.dialogs

import android.view.View
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import io.golos.domain.dto.notifications.NotificationSettingsDomain
import io.golos.domain.dto.notifications.NotificationTypeDomain
import kotlinx.android.synthetic.main.dialog_profile_settings_notifications.*

class ProfileNotificationsDialog(
    private val startSettings: List<NotificationSettingsDomain>
) : BottomSheetDialogFragmentBase<ProfileNotificationsDialog.Result>(showExpanded = true) {

    data class Result(
        val settings: List<NotificationSettingsDomain>
    )
    private lateinit var storedResult: Result

    companion object {
        fun show(parent: Fragment, startSettings: List<NotificationSettingsDomain>, closeAction: (Result?) -> Unit) =
            ProfileNotificationsDialog(startSettings)
                .apply { closeActionListener = closeAction }
                .show(parent.parentFragmentManager, "PROFILE_SETTINGS_DIALOG")
    }

    override val closeButton: View?
        get() = buttonClose

    override val layout: Int
        get() = R.layout.dialog_profile_settings_notifications

    override fun setupView() {
        upvoteNotification.isChecked = startSettings.single { it.type == NotificationTypeDomain.UP_VOTE }.isEnabled
        transferNotification.isChecked = startSettings.single { it.type == NotificationTypeDomain.TRANSFER }.isEnabled
        replyNotification.isChecked = startSettings.single { it.type == NotificationTypeDomain.REPLY }.isEnabled
        mentionNotification.isChecked = startSettings.single { it.type == NotificationTypeDomain.MENTION }.isEnabled
        rewardNotification.isChecked = startSettings.single { it.type == NotificationTypeDomain.REWARD }.isEnabled
        subscribeNotification.isChecked = startSettings.single { it.type == NotificationTypeDomain.SUBSCRIBE }.isEnabled
    }

    override fun onDestroyView() {
        storedResult = Result(listOf(
            NotificationSettingsDomain(NotificationTypeDomain.UP_VOTE, upvoteNotification.isChecked),
            NotificationSettingsDomain(NotificationTypeDomain.TRANSFER, transferNotification.isChecked),
            NotificationSettingsDomain(NotificationTypeDomain.REPLY, replyNotification.isChecked),
            NotificationSettingsDomain(NotificationTypeDomain.MENTION, mentionNotification.isChecked),
            NotificationSettingsDomain(NotificationTypeDomain.REWARD, rewardNotification.isChecked),
            NotificationSettingsDomain(NotificationTypeDomain.SUBSCRIBE, subscribeNotification.isChecked)
        ))

        super.onDestroyView()
    }

    override fun processDestroy() = closeActionListener(storedResult)
}