package io.golos.cyber_android.ui.screens.editor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.base.BaseActivity
import io.golos.domain.interactors.model.DiscussionIdModel

class EditorPageActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context,
                      type: EditorPageViewModel.Type,
                      parentDiscussionIdModel: DiscussionIdModel? = null) =
            Intent(context, EditorPageActivity::class.java).apply {
                putExtra(Tags.POST_TYPE, type)
                if (parentDiscussionIdModel != null) {
                    putExtra(Tags.USER_ID, parentDiscussionIdModel.userId)
                    putExtra(Tags.PERM_LINK, parentDiscussionIdModel.permlink)
                    putExtra(Tags.REF_BLOCK_NUM, parentDiscussionIdModel.refBlockNum)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor_page)
        findNavController(R.id.editorNavHost).setGraph(R.navigation.graph_editor_page, intent.extras)
    }
}
