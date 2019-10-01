package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.widgets

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.UIComponent
import io.golos.domain.AppResourcesProvider
import io.golos.domain.post.post_dto.AttachmentsBlock
import io.golos.domain.post.post_dto.ImageBlock
import io.golos.domain.post.post_dto.VideoBlock
import io.golos.domain.post.post_dto.WebsiteBlock
import kotlinx.android.synthetic.main.view_post_attachments.view.*
import java.lang.UnsupportedOperationException
import javax.inject.Inject

class AttachmentsWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var images = mutableListOf<ImageView>()

    @Inject
    internal lateinit var appResProvider: AppResourcesProvider

    init {
        App.injections.get<UIComponent>().inject(this)

        inflate(context, R.layout.view_post_attachments, this)
    }

    fun render(block: AttachmentsBlock) {
        attachmentsContainer.removeAllViews()

        val size = appResProvider.getDimens(R.dimen.size_post_attachments).toInt()
        val gap = appResProvider.getDimens(R.dimen.gap_post_attachments).toInt()

        val lastIndex = images.lastIndex

        block.content.forEachIndexed() { index, mediaBlock ->
            val uriToShow = when(mediaBlock) {
                is ImageBlock -> mediaBlock.content
                is WebsiteBlock -> mediaBlock.thumbnailUrl ?: Uri.parse(Stubs.website)
                is VideoBlock -> mediaBlock.thumbnailUrl ?: Uri.parse(Stubs.video)
                else -> throw UnsupportedOperationException("This block is not supported: $mediaBlock")
            }

            addAttachment(uriToShow, size, gap, index == 0, index == lastIndex)
        }
    }

    fun cancel() {
        images.forEach {
            Glide.with(this).clear(it)
        }

        images.clear()
    }

    private fun addAttachment(uri: Uri, size: Int, gap: Int, isFirst: Boolean, isLast: Boolean) {
        val imageView = ImageView(context)
        attachmentsContainer.addView(imageView)

        val params = imageView.layoutParams as LinearLayout.LayoutParams

        params.width = size
        params.height = size

        if(!isFirst) {
            params.marginStart = gap
        }

        if(!isLast) {
            params.marginEnd = gap
        }

        imageView.layoutParams = params

        Glide
            .with(this)
            .load(uri)
            .transform(CenterCrop())
            .into(imageView)
    }
}