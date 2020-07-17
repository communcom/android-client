package io.golos.cyber_android.ui.dialogs

import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import kotlinx.android.synthetic.main.dialog_community_settings.*

class CommunitySettingsDialog : BottomSheetDialogFragmentBase<CommunitySettingsDialog.Result>() {

    enum class Result {
        HIDE_COMMUNITY,
        FOLLOW_COMMUNITY,
        SHARE_COMMUNITY
    }

    private var isCommunityHidden: Boolean = false
    private var communityName: String? = null
    private var parent: Fragment? = null

    companion object {
        fun show(parent: Fragment, isCommunityHidden: Boolean, communityName: String, closeAction: (Result?) -> Unit) =
            CommunitySettingsDialog().apply {
                this.parent = parent
                this.isCommunityHidden = isCommunityHidden
                this.communityName = communityName
                closeActionListener = closeAction
            }.show(parent.parentFragmentManager, "COMMUNITY_SETTINGS_DIALOG")
    }

    override val closeButton: View?
        get() = communitySettingsClose
    override val layout: Int
        get() = R.layout.dialog_community_settings

    override fun setupView() {
        title.text = communityName
        if (!isCommunityHidden) {
            hideUnHideButton.text = context?.resources?.getString(R.string.unHide)
            if(context != null) {
                hideUnHideButton.setTextColor(ContextCompat.getColor(context!!, R.color.black))
            }
            context?.getDrawable(R.drawable.ic_unhide_community)?.let {
                hideUnHideButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, it, null)
            }
        } else {
            hideUnHideButton.text = context?.resources?.getString(R.string.hide)
            if(context != null) {
                hideUnHideButton.setTextColor(ContextCompat.getColor(context!!, R.color.badge_color))
            }
            context?.getDrawable(R.drawable.ic_hide_community)?.let {
                hideUnHideButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, it, null)
            }
        }
        share.setOnClickListener { closeOnItemSelected(Result.SHARE_COMMUNITY) }
        hideUnHideButton.setOnClickListener {
            closeOnItemSelected(if (isCommunityHidden) Result.FOLLOW_COMMUNITY
            else Result.HIDE_COMMUNITY)
        }
    }
}


