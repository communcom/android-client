package io.golos.cyber_android.ui.shared_fragments.post.view.widgets

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.ui.common.formatters.time_estimation.TimeEstimationFormatter
import io.golos.cyber_android.ui.shared_fragments.post.dto.PostHeader
import io.golos.domain.AppResourcesProvider
import io.golos.domain.extensions.appendSpannedText
import kotlinx.android.synthetic.main.view_post_viewer_header.view.*
import javax.inject.Inject

/**
 * Header with post info
 */
class PostPageHeader
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onBackButtonClickListener: (() -> Unit)? = null
    private var onJoinToCommunityButtonClickListener: (() -> Unit)? = null
    private var onMenuButtonClickListener: (() -> Unit)? = null

    @Inject
    internal lateinit var appResources: AppResourcesProvider

    init {
        App.injections.get<PostPageFragmentComponent>().inject(this)

        inflate(getContext(), R.layout.view_post_viewer_header, this)

        backButton.setOnClickListener { onBackButtonClickListener?.invoke() }
        joinToCommunityButton.setOnClickListener { onJoinToCommunityButtonClickListener?.invoke() }
        menuButton.setOnClickListener { onMenuButtonClickListener?.invoke() }
    }

    fun setHeader(postHeader: PostHeader) {
        communityTitle.text = postHeader.community.name

        authorAndTime.text = getTimeAndAuthor(postHeader)

        Glide.with(this)
            .load(postHeader.community.logoUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(communityAvatar)
    }

    fun setOnBackButtonClickListener(listener: (() -> Unit)?) {
        onBackButtonClickListener = listener
    }

    fun setOnJoinToCommunityButtonClickListener(listener: (() -> Unit)?) {
        onJoinToCommunityButtonClickListener = listener
    }

    fun setOnMenuButtonClickListener(listener: (() -> Unit)?) {
        onMenuButtonClickListener = listener
    }

    private fun getTimeAndAuthor(postHeader: PostHeader): SpannableStringBuilder {
        val result = SpannableStringBuilder()

        val time = TimeEstimationFormatter(appResources).format(postHeader.actionDateTime)
        result.append(time)

        result.append("\u8226")

        result.appendSpannedText(postHeader.userName, ForegroundColorSpan(appResources.getColor(R.color.blue)))

        return result
    }
}
