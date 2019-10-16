package io.golos.cyber_android.ui.shared_fragments.post.view.view_holders.post_text.widgets

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.domain.AppResourcesProvider
import javax.inject.Inject

/**
 * Post title
 */
class TitleWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr, R.style.PostTitleTextStyle) {

    @Inject
    internal lateinit var appResProvider: AppResourcesProvider

    init {
        App.injections.get<PostPageFragmentComponent>().inject(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        maxLines = 3
        ellipsize = TextUtils.TruncateAt.END

        appResProvider.getDimens(R.dimen.padding_post_paragraph).toInt().also {
            setPadding(it, 0, it, 0)
        }

        val margin = appResProvider.getDimens(R.dimen.margin_block).toInt()

        val params = this.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = margin
        //params.bottomMargin = margin
        layoutParams = params
    }

    fun render(title: String) {
        text = title
    }
}