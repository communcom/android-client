package io.golos.cyber_android.ui.common.widgets.post_comments

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.glide.transformations.TopRoundedCornersTransformation
import io.golos.domain.use_cases.post.post_dto.WebsiteBlock
import io.golos.posts_editor.utilities.post.PostStubs
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

        val thumbnailUrl = block.thumbnailUrl
        //val thumbnailUrl = "https://yastatic.net/s3/home/logos/share/share-logo_ru.png"

        val radius = context.resources.getDimension(R.dimen.radius_corner_embed_website)
        Glide
            .with(this)
            .load(thumbnailUrl?.toString() ?: PostStubs.website)
            .transform(CenterCrop(),
                TopRoundedCornersTransformation(radius)
            )
            .into(image)

        val host = block.content.host?.capitalize()

        leaderName.text = block.title ?: block.description ?: block.providerName ?: host
        siteName.text = host
    }

    override fun release() {
        Glide
            .with(this)
            .clear(image)
        setOnClickProcessor(null)
    }
}