package io.golos.cyber_android.ui.dialogs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.Tags
import io.golos.domain.interactors.model.DiscussionIdModel
import kotlinx.android.synthetic.main.dialog_post_menu.*

/**
 * [BottomSheetDialogFragment] that shows post menu
 */
class PostPageMenuDialog : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_post_menu,
            container,
            false
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //val isMyPost = arguments?.getBoolean("isMyPost")

        edit.setOnClickListener {
            targetFragment?.onActivityResult(targetRequestCode, RESULT_EDIT, Intent().apply {
                putExtra(Tags.DISCUSSION_ID, arguments?.getString("payload"))
            })
            dismiss()
        }

        delete.setOnClickListener {
            targetFragment?.onActivityResult(targetRequestCode, RESULT_DELETE, Intent().apply {
                putExtra(Tags.DISCUSSION_ID, arguments?.getString("payload"))
            })
            dismiss()
        }
    }

    companion object {
        const val RESULT_EDIT = Activity.RESULT_FIRST_USER + 1
        const val RESULT_DELETE = Activity.RESULT_FIRST_USER + 2

        fun newInstance(isMyPost: Boolean, payload: String = ""): PostPageMenuDialog {
            return PostPageMenuDialog().apply {
                arguments = Bundle().apply {
                    putSerializable("isMyPost", isMyPost)
                    putString("payload", payload)
                }
            }
        }
    }
}