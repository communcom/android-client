package io.golos.cyber_android.ui.screens.post

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.base.BaseActivity
import io.golos.domain.interactors.model.PostModel

class PostActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context, post: PostModel) =
            Intent(context, PostActivity::class.java).apply {
                putExtra(Tags.USER_ID, post.contentId.userId)
                putExtra(Tags.PERM_LINK, post.contentId.permlink)
                putExtra(Tags.REF_BLOCK_NUM, post.contentId.refBlockNum)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        findNavController(R.id.postNavHost).setGraph(R.navigation.graph_post, intent.extras)
    }
}
