package io.golos.cyber_android.ui.screens.community_page.child_pages.leads_list.view.list.view_holders

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.characters.SpecialChars
import io.golos.cyber_android.ui.common.extensions.getColorRes
import io.golos.cyber_android.ui.common.extensions.loadAvatar
import io.golos.cyber_android.ui.common.extensions.loadLeader
import io.golos.cyber_android.ui.common.formatters.percent.PercentFormatter
import io.golos.cyber_android.ui.common.formatters.size.PluralSizeFormatter
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.community_page.child_pages.leads_list.dto.LeaderListIem
import io.golos.cyber_android.ui.screens.community_page.child_pages.leads_list.view.list.LeadsListItemEventsProcessor
import io.golos.domain.extensions.appendSpannedText
import kotlinx.android.synthetic.main.view_leaders_list_item.view.*

class LeadersListItemViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<LeadsListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_leaders_list_item
) {
    private val pointsFormatter = PluralSizeFormatter(
        parentView.context.applicationContext,
        R.plurals.formatter_points_formatted
    )

    override fun init(listItem: VersionedListItem, listItemEventsProcessor: LeadsListItemEventsProcessor) {
        if(listItem !is LeaderListIem) {
            return
        }

        with(itemView) {
            ivLogo.loadLeader(listItem.avatarUrl, listItem.ratingPercent.toFloat())
            leaderName.text = listItem.username
            leaderPoints.text = getPointsText(context, listItem)
        }
    }

    override fun release() {
        // do nothing
    }

    private fun getPointsText(context: Context, listItem: LeaderListIem) : SpannableStringBuilder {
        val result = SpannableStringBuilder()

        with(listItem) {
            result.append( pointsFormatter.format(rating.toLong()))

            result.append(" ")
            result.append(" ${SpecialChars.bullet} ")
            result.append(" ")

            result.appendSpannedText(
                PercentFormatter.format(ratingPercent),
                ForegroundColorSpan(context.resources.getColorRes(R.color.blue)))
        }

        return result
    }
}