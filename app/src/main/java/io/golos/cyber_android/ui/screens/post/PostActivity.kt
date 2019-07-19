package io.golos.cyber_android.ui.screens.post

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.base.ActivityBase

class PostActivity : ActivityBase() {

    companion object {
        fun getIntent(context: Context, args: PostPageFragment.Args) =
            Intent(context, PostActivity::class.java).apply {
                putExtra(
                    Tags.ARGS,
                    context.serviceLocator.moshi.adapter(PostPageFragment.Args::class.java)
                        .toJson(args)
                )
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        findNavController(R.id.postNavHost).setGraph(R.navigation.graph_post, intent.extras)
    }
}
