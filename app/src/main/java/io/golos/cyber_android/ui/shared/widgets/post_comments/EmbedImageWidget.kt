package io.golos.cyber_android.ui.shared.widgets.post_comments

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.shared.glide.ImageProgressLoadState
import io.golos.cyber_android.ui.shared.glide.loadContentAttachment
import io.golos.cyber_android.ui.shared.glide.clear
import io.golos.cyber_android.ui.shared.utils.prefetchScreenSize
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.ImageBlock
import kotlinx.android.synthetic.main.layout_image_preload.view.*
import kotlinx.android.synthetic.main.view_post_embed_image.view.*
import kotlinx.android.synthetic.main.view_post_embed_image.view.flPreloadImage

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
    private var cornerRadius: Int = 0
    private var widthBlock: Int = 0
    private @ColorRes var preloadFrameColorId: Int = R.color.post_empty_place_holder

    init {
        inflate(context, R.layout.view_post_embed_image, this)
    }

    fun setContentId(contentId: ContentId?) {
        postContentId = contentId
    }

    fun setCornerRadius(cornerRadius: Int){
        this.cornerRadius = cornerRadius
    }

    fun setWidthBlock(widthBlock: Int){
        this.widthBlock = widthBlock
    }

    fun setPreloadFrameColor(@ColorRes colorId: Int){
        preloadFrameColorId = colorId
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
        imageUri = block.content.prefetchScreenSize(context)
        flPreloadImage.setBackgroundColor(ContextCompat.getColor(context, preloadFrameColorId))
        if (block.description.isNullOrEmpty()) {
            description.visibility = View.GONE
        } else {
            description.text = block.description
            description.visibility = View.VISIBLE
        }
        val contentImage: ImageView
        val imageHeight = block.height
        val imageWidth = block.width
        if(imageWidth == null || imageHeight == null){
            contentImage = imageAspectRatio
            imageAspectRatio.visibility = View.VISIBLE
            image.visibility = View.GONE
        } else{
            contentImage = image
            contentImage.visibility = View.VISIBLE
            imageAspectRatio.visibility = View.GONE
            val layoutParams = image.layoutParams as ConstraintLayout.LayoutParams
            if(widthBlock == 0){
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            } else{
                val heightBlock = widthBlock.toFloat() / imageWidth * imageHeight
                layoutParams.height = heightBlock.toInt()
            }
            image.layoutParams = layoutParams
        }
        btnRetry.setOnClickListener {
            loadImage(contentImage, imageUri.toString())
        }
        loadImage(contentImage, imageUri.toString())
    }

    private fun loadImage(imageView: ImageView, url: String?){
        imageView.loadContentAttachment(url, {
            when(it){
                ImageProgressLoadState.START -> {
                    pbImageLoad.visibility = View.VISIBLE
                    flPreloadImage.visibility = View.VISIBLE
                    btnRetry.visibility = View.INVISIBLE
                }
                ImageProgressLoadState.COMPLETE -> {
                    pbImageLoad.visibility = View.INVISIBLE
                    btnRetry.visibility = View.INVISIBLE
                    flPreloadImage.visibility = View.GONE
                }
                ImageProgressLoadState.ERROR -> {
                    pbImageLoad.visibility = View.INVISIBLE
                    btnRetry.visibility = View.VISIBLE
                }
            }
        }, cornerRadius)
    }

    override fun release() {
        image.clear()
        imageAspectRatio.clear()
        setOnClickProcessor(null)
        setOnClickListener(null)
    }
}