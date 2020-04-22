package io.golos.cyber_android.ui.shared.widgets.post_comments

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.LinearLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.glide.clear
import io.golos.cyber_android.ui.shared.glide.loadWebsiteContent
import io.golos.cyber_android.ui.shared.utils.prefetchScreenSize
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.WebsiteBlock
import kotlinx.android.synthetic.main.view_post_embed_website.view.*

class EmbedWebsiteWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr),
    BlockWidget<WebsiteBlock, EmbedWebsiteWidgetListener> {

    private var onClickProcessor: EmbedWebsiteWidgetListener? = null
    private var siteUri: Uri? = null

    init {
        inflate(context, R.layout.view_post_embed_website, this)
    }

    override fun setOnClickProcessor(processor: EmbedWebsiteWidgetListener?) {
        if(processor != null) {
            setOnClickListener {
                siteUri?.let {
                    this.onClickProcessor?.onLinkClicked(it)
                }
            }
        } else {
            setOnClickListener(null)
        }
        this.onClickProcessor = processor
    }

    @SuppressLint("DefaultLocale")
    override fun render(block: WebsiteBlock) {
        siteUri = block.content

        val thumbnailUrl = block.thumbnailUrl?.prefetchScreenSize(context)
        image.loadWebsiteContent(thumbnailUrl?.toString())

        val host = block.content.host?.capitalize()

        leaderName.text = block.title ?: block.description ?: block.providerName ?: host
        siteName.text = host
    }

    override fun release() {
        image.clear()
        setOnClickProcessor(null)
    }
}