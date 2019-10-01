package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import io.golos.cyber_android.R
import io.golos.domain.post.post_dto.ImageBlock
import kotlinx.android.synthetic.main.view_post_embed_image.view.*

class EmbedImageWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr),
    PostBlockWidget<ImageBlock> {

    init {
        inflate(context, R.layout.view_post_embed_image, this)
    }

    override fun render(block: ImageBlock) {
        description.text = block.description

        Glide
            .with(this)
            .load(block.content)
            .transform(CenterCrop())
            .into(image)
    }

    override fun cancel() {
        Glide.with(this).clear(image)
    }
}