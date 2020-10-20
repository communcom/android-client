package io.golos.cyber_android.ui.shared.popups.app_update

import android.net.Uri
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.ui.shared.utils.openLinkView

class AppUpdatePopup(
    contentView: View,
    private val parent: LifecycleOwner
): PopupWindow(
    contentView,
    ViewGroup.LayoutParams.MATCH_PARENT,
    ViewGroup.LayoutParams.MATCH_PARENT
), LifecycleObserver {

    companion object {
        private val popups = mutableMapOf<LifecycleOwner, AppUpdatePopup>()

        fun show(parent: LifecycleOwner, root: ViewGroup) {
            val oldPopup = popups[parent]
            if(oldPopup != null) {
                return
            }

            val view = AppUpdateWidget(context = root.context)
            view.setOnUpdateClickListener {
                val uriToOpen = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")

                when(parent) {
                    is AppCompatActivity -> parent.openLinkView(uriToOpen)
                    is Fragment -> parent.context!!.openLinkView(uriToOpen)
                    else -> throw  UnsupportedOperationException("This type of parent object is not supported")
                }
            }

            popups[parent] = AppUpdatePopup(
                view,
                parent
            )
                .apply {
                    showAtLocation(root, Gravity.CENTER, 0, 0)
                    parent.lifecycle.addObserver(this)
                }
        }

        fun hide(parent: LifecycleOwner) {
            popups.remove(parent)?.dismiss()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onParentDestroy() {
        hide(parent)
    }
}