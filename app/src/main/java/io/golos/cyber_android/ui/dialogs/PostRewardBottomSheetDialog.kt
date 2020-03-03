package io.golos.cyber_android.ui.dialogs

import android.net.Uri
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.utils.openLinkView
import kotlinx.android.synthetic.main.dialog_simple_text.*

class PostRewardBottomSheetDialog(
    @StringRes titleResId: Int,
    @StringRes textResId: Int
) : SimpleTextBottomSheetDialog(titleResId, textResId, R.string.post_reward_main_button_text) {

    companion object {
        fun show(
            parent: Fragment,
            @StringRes titleResId: Int,
            @StringRes textResId: Int,
            closeAction: (Result?) -> Unit) =
            PostRewardBottomSheetDialog(titleResId, textResId)
                .apply { closeActionListener = closeAction }
                .show(parent.parentFragmentManager, "POST_REWARD_BOTTOM_SHEET_DIALOG")
    }

    override fun setupView() {
        super.setupView()

        mainButton.setOnClickListener {
            closeOnItemSelected(Result.Ok)
            this@PostRewardBottomSheetDialog.context!!.openLinkView(Uri.parse("https://commun.com/faq#What%20else%20can%20you%20do%20with%20the%20points"))
        }
    }
}