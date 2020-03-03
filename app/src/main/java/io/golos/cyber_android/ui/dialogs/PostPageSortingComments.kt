package io.golos.cyber_android.ui.dialogs

import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import kotlinx.android.synthetic.main.dialog_comments_sorting_menu.*

/**
 * [BottomSheetDialogFragment] that shows comments sorting menu
 */
class PostPageSortingComments : BottomSheetDialogFragmentBase<PostPageSortingComments.Result>() {
    sealed class Result {
        object InterestingFirst : Result()
        object ByTime : Result()
    }

    companion object {
        fun show(parent: Fragment, closeAction: (Result?) -> Unit) =
            PostPageSortingComments()
                .apply { closeActionListener = closeAction }
                .show(parent.parentFragmentManager, "POST_PAGE_SORTING_COMMENTS_DIALOG")


    }

    override val closeButton: Button?
        get() = null

    override val layout: Int
        get() = R.layout.dialog_comments_sorting_menu

    override fun setupView() {
        interestingFirst.setOnClickListener { closeOnItemSelected(Result.InterestingFirst) }
        byTime.setOnClickListener { closeOnItemSelected(Result.ByTime) }
    }
}