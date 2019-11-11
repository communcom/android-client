package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.ui.common.glide.TopRoundedCorners
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelListEventsProcessor
import io.golos.domain.AppResourcesProvider
import io.golos.domain.use_cases.post.post_dto.WebsiteBlock
import io.golos.posts_editor.utilities.post.PostStubs
import kotlinx.android.synthetic.main.view_post_embed_website.view.*
import javax.inject.Inject

class EmbedWebsiteWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr),
    PostBlockWidget<WebsiteBlock> {

    private var onClickProcessor: PostPageViewModelListEventsProcessor? = null
    private var siteUri: Uri? = null

    @Inject
    internal lateinit var appResourcesProvider: AppResourcesProvider

    init {
        App.injections.get<PostPageFragmentComponent>().inject(this)

        inflate(context, R.layout.view_post_embed_website, this)
    }

    override fun setOnClickProcessor(processor: PostPageViewModelListEventsProcessor?) {
        if(processor != null) {
            setOnClickListener {
                siteUri?.let {
                    this.onClickProcessor?.onLinkInPostClick(it)
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

        val radius = appResourcesProvider.getDimens(R.dimen.radius_corner_embed_website)
        Glide
            .with(this)
            .load(thumbnailUrl?.toString() ?: PostStubs.website)
            .transform(CenterCrop(), TopRoundedCorners(radius))
            .into(image)

        val host = block.content.host?.capitalize()

        title.text = block.title ?: block.description ?: block.providerName ?: host
        siteName.text = host
    }

    override fun cancel() {
        Glide
            .with(this)
            .clear(image)
        setOnClickProcessor(null)
    }
}