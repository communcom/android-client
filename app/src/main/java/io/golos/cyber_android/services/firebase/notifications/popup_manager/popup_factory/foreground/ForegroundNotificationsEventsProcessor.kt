package io.golos.cyber_android.services.firebase.notifications.popup_manager.popup_factory.foreground

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.skydoves.balloon.Balloon
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import io.golos.cyber_android.ui.screens.notifications.view.list.items.BaseNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.utils.IntentConstants
import io.golos.domain.dto.UserIdDomain
import timber.log.Timber
import java.lang.ref.WeakReference

class ForegroundNotificationsEventsProcessor(
    activity: AppCompatActivity,
    balloon: Balloon
) : NotificationsViewModelListEventsProcessor {

    private var currentActivity = WeakReference(activity)
    private var currentBalloon = WeakReference(balloon)

    override fun loadMoreNotifications() { }

    override fun onChangeFollowerStatusClicked(notification: BaseNotificationItem) { }

    override fun onUserClickedById(userId: UserIdDomain) {
        openNotification { intent -> intent.putExtra(IntentConstants.USER_ID, userId) }
    }

    override fun onPostNavigateClicked(contentId: ContentId) {
        openNotification { intent -> intent.putExtra(IntentConstants.POST_CONTENT_ID, contentId) }
    }

    override fun onWalletNavigateClicked() {
        openNotification { intent -> intent.putExtra(IntentConstants.WALLET, true) }
    }

    override fun onRetryLoadPage() { }

    private fun openNotification(intentDataAction: (Intent) -> Unit) {
        currentBalloon.get()?.dismiss()

        try {
            currentActivity.get()
                ?.takeIf { !it.isDestroyed && !it.isFinishing  }
                ?.let { activity ->
                    val  isMainActivity = activity is MainActivity
                    val intent = Intent(activity,  MainActivity::class.java)
                        .apply {
                            val flag = if(isMainActivity) {
                                Intent.FLAG_ACTIVITY_SINGLE_TOP }
                            else {
                                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                            }
                            addFlags(flag)

                            action = IntentConstants.ACTION_OPEN_NOTIFICATION
                            intentDataAction(this)
                        }

                    activity.startActivity(intent)
                    if(!isMainActivity) {
                        activity.finish()
                    }
                }
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }
}