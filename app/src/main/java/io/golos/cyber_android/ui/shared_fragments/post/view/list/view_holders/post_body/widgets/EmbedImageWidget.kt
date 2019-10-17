package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelItemsClickProcessor
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

    private var onClickProcessor: PostPageViewModelItemsClickProcessor? = null
    private var imageUri: Uri? = null

    init {
        inflate(context, R.layout.view_post_embed_image, this)
    }

    override fun setOnClickProcessor(processor: PostPageViewModelItemsClickProcessor?) {
        if(processor != null) {
            setOnClickListener {
                imageUri?.let {
                    this.onClickProcessor?.onImageInPostClick(it)
                }
            }
        } else {
            setOnClickListener(null)
        }
        this.onClickProcessor = processor
    }

    override fun render(block: ImageBlock) {
        imageUri = block.content

        description.text = block.description

        Glide
            .with(this)
            .load(block.content)
            .transform(CenterCrop())
            .into(image)
    }

    override fun cancel() {
        Glide.with(this).clear(image)
        setOnClickProcessor(null)
    }
}