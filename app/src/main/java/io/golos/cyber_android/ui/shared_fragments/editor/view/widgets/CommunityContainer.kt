package io.golos.cyber_android.ui.shared_fragments.editor.view.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.extensions.loadCommunity
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
    }

    fun setCommunity(community: CommunityDomain?) {
        if(community == null) {
            return
        }

        communityName.text = community.name

        communityAvatar.loadCommunity(community.avatarUrl)
    }

    fun setOnShowCommunitiesClickListener(listener: () -> Unit) {
        onShowCommunitiesClickListener = listener
    }

    fun setSelectCommunityEnabled(isEnabled: Boolean) {
        showCommunitiesButton.isEnabled = isEnabled
    }
}
