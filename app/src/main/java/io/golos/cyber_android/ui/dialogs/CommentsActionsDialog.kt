package io.golos.cyber_android.ui.dialogs

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.android.synthetic.main.dialog_comment_menu.*

/**
 * [BottomSheetDialogFragment] that shows comment menu
 */
class CommentsActionsDialog(
    private val commentId: DiscussionIdModel
) : BottomSheetDialogFragmentBase<CommentsActionsDialog.Result>() {

    sealed class Result {
        data class Edit (val commentId: DiscussionIdModel): Result()
        data class Delete (val commentId: DiscussionIdModel): Result()
    }

    companion object {
        fun show(parent: Fragment, commentId: DiscussionIdModel, closeAction: (Result?) -> Unit) =
            CommentsActionsDialog(commentId)
                .apply { closeActionListener = closeAction }
                .show(parent.parentFragmentManager, "COMMENTS_ACTIONS_DIALOG")
    }

    override val closeButton: View?
        get() = null

    override val layout: Int
        get() = R.layout.dialog_comment_menu

    override fun setupView() {
        edit.setOnClickListener { closeOnItemSelected(Result.Edit(commentId)) }
        delete.setOnClickListener { closeOnItemSelected(Result.Delete(commentId)) }
    }
}