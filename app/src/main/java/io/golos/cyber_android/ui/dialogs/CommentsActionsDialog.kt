package io.golos.cyber_android.ui.dialogs

import android.app.Activity
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.golos.cyber_android.R
import io.golos.domain.interactors.model.DiscussionIdModel
import kotlinx.android.synthetic.main.dialog_comment_menu.*

/**
 * [BottomSheetDialogFragment] that shows comment menu
 */
class CommentsActionsDialog : BottomSheetDialogFragmentBase() {
    companion object {
        const val REQUEST = 3486

        const val RESULT_EDIT = Activity.RESULT_FIRST_USER + 1
        const val RESULT_DELETE = Activity.RESULT_FIRST_USER + 2

        const val COMMENT_ID = "COMMENT_ID"

        fun newInstance(commentId: DiscussionIdModel): CommentsActionsDialog {
            return CommentsActionsDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(COMMENT_ID, commentId)
                }
            }
        }
    }

    override fun provideLayout(): Int = R.layout.dialog_comment_menu

    override fun setupView() {
        val commentId = arguments!!.getParcelable<DiscussionIdModel>(COMMENT_ID)!!

        edit.setSelectAction(RESULT_EDIT) {
            putExtra(COMMENT_ID, commentId)
        }

        delete.setSelectAction(RESULT_DELETE) {
            putExtra(COMMENT_ID, commentId)
        }
    }
}