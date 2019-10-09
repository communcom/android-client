package io.golos.cyber_android.ui.shared_fragments.editor.view.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import io.golos.domain.commun_entities.Community
import kotlinx.android.synthetic.main.view_post_editor_community_container.view.*

/**
 * "Button" with some primary text, a secondary text and an icon
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

    fun setCommunity(community: Community) {
        communityName.text = community.name

        Glide.with(this)
            .load(community.logoUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(communityAvatar)
    }

    fun setOnShowCommunitiesClickListener(listener: () -> Unit) {
        onShowCommunitiesClickListener = listener
    }
}
