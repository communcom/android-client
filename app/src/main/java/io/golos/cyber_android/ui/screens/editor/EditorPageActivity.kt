package io.golos.cyber_android.ui.screens.editor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.base.BaseActivity

class EditorPageActivity : BaseActivity() {

    companion object {
        fun getIntent(
            context: Context,
            args: EditorPageFragment.Args = EditorPageFragment.Args()
        ) =
            Intent(context, EditorPageActivity::class.java).apply {
                putExtra(
                    Tags.ARGS,
                    context.serviceLocator.moshi.adapter(EditorPageFragment.Args::class.java)
                        .toJson(args)
                )
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor_page)
        findNavController(R.id.editorNavHost).setGraph(R.navigation.graph_editor_page, intent.extras)
    }
}
