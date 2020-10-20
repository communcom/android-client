package io.golos.cyber_android.ui.screens.post_edit.fragment.view.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.glide.loadCommunity
import io.golos.domain.dto.CommunityDomain
import kotlinx.android.synthetic.main.view_post_editor_community_container.view.*

/**
 * Header with community info
 */
class CommunityContainer
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onShowCommunitiesClickListener: (() -> Unit)? = null

    init {
        inflate(getContext(), R.layout.view_post_editor_community_container, this)

        showCommunitiesButton.setOnClickListener { onShowCommunitiesClickListener?.invoke() }
        emptyCommunityName.setOnClickListener { onShowCommunitiesClickListener?.invoke() }
        communityTitle.setOnClickListener { onShowCommunitiesClickListener?.invoke() }
        communityAvatar.setOnClickListener { onShowCommunitiesClickListener?.invoke() }
    }

    fun setCommunity(community: CommunityDomain?) {
        if(community == null) {
            return
        }

        communityName.text = community.name
        communityName.visibility = View.VISIBLE
        emptyCommunityName.visibility = View.INVISIBLE
        communityAvatar.loadCommunity(community.avatarUrl)
    }

    fun setOnShowCommunitiesClickListener(listener: () -> Unit) {
        onShowCommunitiesClickListener = listener
    }

    fun setSelectCommunityEnabled(isEnabled: Boolean) {
        showCommunitiesButton.isEnabled = isEnabled

        if(isEnabled) {
            emptyCommunityName.setOnClickListener { onShowCommunitiesClickListener?.invoke() }
            communityTitle.setOnClickListener { onShowCommunitiesClickListener?.invoke() }
            communityAvatar.setOnClickListener { onShowCommunitiesClickListener?.invoke() }
        } else {
            emptyCommunityName.setOnClickListener(null)
            communityTitle.setOnClickListener(null)
            communityAvatar.setOnClickListener(null)
        }
    }
}
