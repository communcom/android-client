package io.golos.cyber_android.ui.dialogs

import android.net.Uri
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.utils.openLinkView
import kotlinx.android.synthetic.main.dialog_simple_text.*

class PostRewardBottomSheetDialog : SimpleTextBottomSheetDialog() {
    companion object {
        fun newInstance(
            target: Fragment,
            @StringRes titleResId: Int,
            @StringRes textResId: Int
        ): SimpleTextBottomSheetDialog {

            return PostRewardBottomSheetDialog().apply {
                arguments = Bundle().apply {
                    putInt(TITLE_KEY, titleResId)
                    putInt(TEXT_KEY, textResId)
                    putInt(MAIN_BUTTON_TEXT_KEY, R.string.post_reward_main_button_text)
                }
                setTargetFragment(target, REQUEST)
            }
        }
    }

    override fun setupView() {
        super.setupView()

        mainButton.setSelectAction(RESULT_MAIN_ACTION) {
            this@PostRewardBottomSheetDialog.context!!.openLinkView(Uri.parse("https://commun.com/faq#What%20else%20can%20you%20do%20with%20the%20points"))
        }
    }
}