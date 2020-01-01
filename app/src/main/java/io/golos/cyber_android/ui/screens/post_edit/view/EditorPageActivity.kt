package io.golos.cyber_android.ui.screens.post_edit.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.Tags
import io.golos.cyber_android.ui.shared.base.ActivityBase

class EditorPageActivity : ActivityBase() {

    companion object {
        fun getIntent(context: Context, args: EditorPageFragment.Args = EditorPageFragment.Args()) =
            Intent(context, EditorPageActivity::class.java).apply { putExtra(Tags.ARGS, args) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor_page)
        findNavController(R.id.editorNavHost).setGraph(R.navigation.graph_editor_page, intent.extras)
    }
}
