package io.golos.cyber_android.ui.dialogs

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.Tags
import io.golos.domain.use_cases.post.post_dto.PostFormatVersion
import io.golos.domain.use_cases.post.post_dto.PostType
import io.golos.domain.posts_parsing_rendering.PostGlobalConstants
import kotlinx.android.synthetic.main.dialog_comment_menu.*
import kotlinx.android.synthetic.main.dialog_post_menu.*

/**
 * [BottomSheetDialogFragment] that shows post menu
 */
class PostPageMenuDialog : BottomSheetDialogFragmentBase() {
    companion object {
        const val REQUEST = 9303

        const val RESULT_EDIT = Activity.RESULT_FIRST_USER + 1
        const val RESULT_DELETE = Activity.RESULT_FIRST_USER + 2

        private const val IS_MY_POST = "IS_MY_POST"
        private const val PAYLOAD = "PAYLOAD"
        private const val TYPE = "TYPE"
        private const val FORMAT_VERSION = "FORMAT_VERSION"

        fun newInstance(isMyPost: Boolean, type: PostType, formatVersion: PostFormatVersion, payload: String = ""): PostPageMenuDialog {
            return PostPageMenuDialog().apply {
                arguments = Bundle().apply {
                    putSerializable(IS_MY_POST, isMyPost)
                    putString(PAYLOAD, payload)
                    putInt(TYPE, type.value)
                    putParcelable(FORMAT_VERSION, formatVersion)
                }
            }
        }
    }

    override fun provideLayout(): Int = R.layout.dialog_post_menu

    override fun setupView() {
        //val isMyPost = arguments?.getBoolean(IS_MY_POST)
        val type = PostType.create(arguments!!.getInt(TYPE))
        val postFormat = arguments!!.getParcelable<PostFormatVersion>(FORMAT_VERSION)!!

        val canEdit = type == PostType.BASIC && PostGlobalConstants.postFormatVersion.major >= postFormat.major

        edit.visibility = if(canEdit) View.VISIBLE else View.GONE

        edit.setSelectAction(RESULT_EDIT) {
            putExtra(Tags.DISCUSSION_ID, arguments?.getString(PAYLOAD))
        }

        delete.setSelectAction(RESULT_DELETE) {
            putExtra(Tags.DISCUSSION_ID, arguments?.getString(PAYLOAD))
        }
    }
}