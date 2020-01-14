package io.golos.cyber_android.ui.shared.widgets.post_comments

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.shared.glide.release
import io.golos.domain.use_cases.post.post_dto.ImageBlock
import kotlinx.android.synthetic.main.view_post_embed_image.view.*

class EmbedImageWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr),
    BlockWidget<ImageBlock, EmbedImageWidgetListener> {

    private var onClickProcessor: EmbedImageWidgetListener? = null
    private var imageUri: Uri? = null

    private var postContentId: ContentId? = null

    init {
        inflate(context, R.layout.view_post_embed_image, this)
    }

    fun setContentId(contentId: ContentId?) {
        postContentId = contentId
    }

    override fun setOnClickProcessor(processor: EmbedImageWidgetListener?) {
        if (postContentId != null) {
            setOnClickListener {
                postContentId?.let { id ->
                    processor?.onItemClicked(id)
                }
            }
        } else {
            if (processor != null) {
                setOnClickListener {
                    imageUri?.let {
                        this.onClickProcessor?.onImageClicked(it)
                    }
                }
            } else {
                setOnClickListener(null)
            }
            this.onClickProcessor = processor
        }
    }

    override fun render(block: ImageBlock) {
        imageUri = block.content

        if (block.description.isNullOrEmpty()) {
            description.visibility = View.GONE
        } else {
            description.text = block.description
            description.visibility = View.VISIBLE
        }
        val contentImage: ImageView
        if(block.width == null || block.height == null){
            contentImage = imageAspectRatio
            image.visibility = View.GONE
        } else{
            contentImage = image
            imageAspectRatio.visibility = View.GONE
        }
        Glide
            .with(this)
            .load(block.content)
            .into(contentImage)
    }

    override fun release() {
        image.release()
        imageAspectRatio.release()
        setOnClickProcessor(null)
        setOnClickListener(null)
    }
}