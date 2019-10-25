package io.golos.cyber_android.ui.dialogs

import android.app.Activity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.dialog_comments_sorting_menu.*

/**
 * [BottomSheetDialogFragment] that shows comments sorting menu
 */
class PostPageSortingComments : BottomSheetDialogFragmentBase() {
    companion object {
        const val REQUEST = 5045

        const val RESULT_INTERESTING_FIRST = Activity.RESULT_FIRST_USER + 3
        const val RESULT_BY_TIME = Activity.RESULT_FIRST_USER + 4

        fun newInstance(): PostPageSortingComments = PostPageSortingComments()
    }

    override fun provideLayout(): Int = R.layout.dialog_comments_sorting_menu

    override fun setupView() {
        interestingFirst.setSelectAction(RESULT_INTERESTING_FIRST)
        byTime.setSelectAction(RESULT_BY_TIME)
    }
}