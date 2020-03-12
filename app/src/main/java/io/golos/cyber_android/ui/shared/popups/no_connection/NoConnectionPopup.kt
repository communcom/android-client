package io.golos.cyber_android.ui.shared.popups.no_connection

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

class NoConnectionPopup(
    contentView: View,
    private val parent: LifecycleOwner
): PopupWindow(
    contentView,
    ViewGroup.LayoutParams.MATCH_PARENT,
    ViewGroup.LayoutParams.MATCH_PARENT
), LifecycleObserver {

    companion object {
        private val popups = mutableMapOf<LifecycleOwner, NoConnectionPopup>()

        fun show(parent: LifecycleOwner, root: ViewGroup, reconnectListener: () -> Unit) {
            val oldPopup = popups[parent]
            if(oldPopup != null) {
                return
            }

            val view =
                NoConnectionWidget(context = root.context)
            view.setOnReconnectClickListener(reconnectListener)

            popups[parent] = NoConnectionPopup(
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