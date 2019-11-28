package io.golos.cyber_android.ui.common.widgets.post

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import io.golos.cyber_android.R
import io.golos.domain.use_cases.post.post_dto.*
import io.golos.posts_editor.utilities.post.PostStubs
import kotlinx.android.synthetic.main.view_post_attachments.view.*

class AttachmentsWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    PostBlockWidget<AttachmentsBlock, AttachmentWidgetListener> {

    private var images = mutableListOf<ImageView>()

    init {
        inflate(context, R.layout.view_post_attachments, this)
    }

    override fun render(block: AttachmentsBlock) {
        attachmentsContainer.removeAllViews()

        val size = context.resources.getDimension(R.dimen.size_post_attachments).toInt()
        val gap = context.resources.getDimension(R.dimen.gap_post_attachments).toInt()

        val lastIndex = images.lastIndex

        block.content.forEachIndexed { index, mediaBlock ->
            when (mediaBlock) {
                is RichBlock -> attachmentsContainer.addView(RichWidget(context))
                is EmbedBlock -> attachmentsContainer.addView(EmbedWidget(context))
                else -> {
                    val uriToShow = when (mediaBlock) {
                        is ImageBlock -> mediaBlock.content
                        is WebsiteBlock -> mediaBlock.thumbnailUrl ?: Uri.parse(PostStubs.website)
                        is VideoBlock -> mediaBlock.thumbnailUrl ?: Uri.parse(PostStubs.video)
                        else -> throw UnsupportedOperationException("This block is not supported: $mediaBlock")
                    }

                    addAttachment(uriToShow, size, gap, index == 0, index == lastIndex)
                }
            }
        }
    }

    override fun release() {
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