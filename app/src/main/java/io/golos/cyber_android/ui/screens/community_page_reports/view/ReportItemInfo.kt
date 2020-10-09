package io.golos.cyber_android.ui.screens.community_page_reports.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.domain.dto.EntityReportDomain
import kotlinx.android.synthetic.main.view_report_users_item.view.*

class ReportItemInfo
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        inflate(getContext(), R.layout.view_report_users_item, this)

    }

    @SuppressLint("SetTextI18n")
    fun init(entityReportDomain: EntityReportDomain) {
        avatar.loadAvatar(entityReportDomain.author?.avatarUrl)
        name.text = entityReportDomain.author?.username
        vReportInfo.text = " ${entityReportDomain.reason}"

    }
}