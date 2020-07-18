package io.golos.cyber_android.ui.screens.post_report.view

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import io.golos.domain.dto.ContentIdDomain
import kotlinx.android.synthetic.main.dialog_report_other_reason.*

class PostReportOtherReasonDialog private constructor() : BottomSheetDialogFragmentBase<String?>() {

    override val closeButton: View?
        get() = ivClose
    override val layout: Int
        get() = R.layout.dialog_report_other_reason

    private var communityTitle: String? = null

    companion object {
        fun show(parent: Fragment, contentIdDomain: ContentIdDomain, closeAction: (String?) -> Unit) =
            PostReportOtherReasonDialog().apply {
                communityTitle = contentIdDomain.communityId.code
                closeActionListener = closeAction
            }.show(parent.parentFragmentManager, "REPORT_MESSAGE_DIALOG")
    }

    override fun setupView() {
        tvTitle.text = "Report to $communityTitle"
        btnSend.isEnabled = false
        editorWidget.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                btnSend.isEnabled = p0?.isNotEmpty() == true
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })
        btnSend.setOnClickListener { closeOnItemSelected(editorWidget.text.toString()) }
    }
}