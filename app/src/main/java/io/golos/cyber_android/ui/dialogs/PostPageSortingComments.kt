package io.golos.cyber_android.ui.dialogs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.dialog_comments_sorting_menu.*

/**
 * [BottomSheetDialogFragment] that shows comments sorting menu
 */
class PostPageSortingComments : BottomSheetDialogFragment() {
    companion object {
        const val RESULT_INTERESTING_FIRST = Activity.RESULT_FIRST_USER + 3
        const val RESULT_BY_TIME = Activity.RESULT_FIRST_USER + 4

        fun newInstance(): PostPageSortingComments = PostPageSortingComments()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogFragment_RoundCorners)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.dialog_comments_sorting_menu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        interestingFirst.setOnClickListener {
            targetFragment?.onActivityResult(targetRequestCode, RESULT_INTERESTING_FIRST, Intent())
            dismiss()
        }

        byTime.setOnClickListener {
            targetFragment?.onActivityResult(targetRequestCode, RESULT_BY_TIME, Intent())
            dismiss()
        }
    }
}