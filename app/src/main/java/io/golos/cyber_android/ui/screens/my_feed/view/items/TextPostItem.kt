package io.golos.cyber_android.ui.screens.my_feed.view.items

import android.content.Context
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.base.adapter.BaseRecyclerItem
import io.golos.cyber_android.ui.dto.document.Paragraph
import kotlinx.android.synthetic.main.item_feed_text.view.*

class TextPostItem(
    private val paragraph: Paragraph.Text
) : BaseRecyclerItem() {

    override fun getLayoutId(): Int = R.layout.item_feed_text

    override fun renderView(context: Context, view: View) {
        super.renderView(context, view)
        view.feedText.text = paragraph.content
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TextPostItem

        if (paragraph != other.paragraph) return false

        return true
    }

    override fun hashCode(): Int {
        return paragraph.hashCode()
    }


}