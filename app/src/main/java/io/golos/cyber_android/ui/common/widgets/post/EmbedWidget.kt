package io.golos.cyber_android.ui.common.widgets.post

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import io.golos.cyber_android.R
import io.golos.domain.use_cases.post.post_dto.EmbedBlock
import kotlinx.android.synthetic.main.view_attachment_rich.view.*

class EmbedWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    PostBlockWidget<EmbedBlock, EmbedWidgetListener> {

    init {
        inflate(context, R.layout.view_attachment_rich, this)
    }

    override fun render(block: EmbedBlock) {
        val thumbnailUrl = block.thumbnailUrl
        if (thumbnailUrl != null) {
            val width = block.thumbnailWidth ?: 640
            val height = block.thumbnailHeight ?: 640
            richImage.visibility = View.VISIBLE
            richDescription.visibility = View.GONE
            Glide.with(context)
                .load(thumbnailUrl)
                .override(width, height)
                .into(richImage)
        } else {
            richImage.visibility = View.GONE
            richDescription.visibility = View.VISIBLE
            richDescription.text = block.description
        }

        val type = Type.getTypeByProviderName(block.providerName.orEmpty())
        richIcon.setImageResource(type.providerIconResId)
        richName.text = block.author
        richUrl.text = block.authorUrl?.authority
    }

    override fun release() {
        Glide.with(context).clear(richImage)
    }

    enum class Type(
        val providerName: String,
        @DrawableRes val providerIconResId: Int
    ) {
        INSTAGRAM("instagram", R.drawable.ic_instagram),
        TWITTER("twitter", R.drawable.ic_twitter),
        OTHER("", R.drawable.ic_rich_other);

        companion object {
            fun getTypeByProviderName(providerName: String): Type {
                return values().find { type -> type.providerName == providerName } ?: OTHER
            }
        }
    }
}