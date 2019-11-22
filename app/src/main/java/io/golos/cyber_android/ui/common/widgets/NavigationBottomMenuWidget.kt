package io.golos.cyber_android.ui.common.widgets

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.view_navigation_bottom_menu.view.*

class NavigationBottomMenuWidget : MaterialCardView {

    var clickListener: Listener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        inflate(context, R.layout.view_navigation_bottom_menu, this)

        homeMenu.isSelected = true
        homeMenu.setOnClickListener {
            clickListener?.onFeedClick()
            setUpFeedMenuSelection()
        }
        communityMenu.setOnClickListener {
            clickListener?.onCommunityClick()
            setUpCommunityMenuSelection()
        }
        createPostMenu.setOnClickListener {
            clickListener?.onCreateClick()
        }
        notificationMenu.setOnClickListener {
            clickListener?.onNotificationClick()
            setUpNotificationMenuSelection()
        }
        profileMenu.setOnClickListener {
            clickListener?.onProfileClick()
            setUpProfileMenuSelection()
        }
    }

    private fun setUpFeedMenuSelection() {
        homeMenu.isSelected = true
        communityMenu.isSelected = false
        notificationMenu.isSelected = false
        profileMenu.isSelected = false
    }

    private fun setUpCommunityMenuSelection() {
        homeMenu.isSelected = false
        communityMenu.isSelected = true
        notificationMenu.isSelected = false
        profileMenu.isSelected = false
    }

    private fun setUpNotificationMenuSelection() {
        homeMenu.isSelected = false
        communityMenu.isSelected = false
        notificationMenu.isSelected = true
        profileMenu.isSelected = false
    }

    private fun setUpProfileMenuSelection() {
        homeMenu.isSelected = false
        communityMenu.isSelected = false
        notificationMenu.isSelected = false
        profileMenu.isSelected = true
    }

    interface Listener {

        fun onFeedClick(tab: Tab = Tab.FEED)

        fun onCommunityClick(tab: Tab = Tab.COMMUNITIES)

        fun onCreateClick()

        fun onNotificationClick(tab: Tab = Tab.NOTIFICATIONS)

        fun onProfileClick(tab: Tab = Tab.PROFILE)
    }

    enum class Tab(val index: Int) {
        FEED(0),
        COMMUNITIES(1),
        NOTIFICATIONS(2),
        PROFILE(3)
    }
}