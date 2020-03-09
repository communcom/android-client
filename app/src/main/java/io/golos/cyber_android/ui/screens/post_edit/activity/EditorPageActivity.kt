package io.golos.cyber_android.ui.screens.post_edit.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.post_edit.activity.di.EditorPageActivityComponent
import io.golos.cyber_android.ui.screens.post_edit.fragment.view.EditorPageFragment
import io.golos.cyber_android.ui.screens.post_edit.shared.EditorPageBridgeActivity
import io.golos.cyber_android.ui.shared.Tags
import io.golos.cyber_android.ui.shared.base.ActivityBase
import io.golos.utils.id.IdUtil
import javax.inject.Inject

class EditorPageActivity : ActivityBase() {
    companion object {
        private const val INJECTION_KEY = "INJECTION_KEY"

        fun getIntent(context: Context, args: EditorPageFragment.Args = EditorPageFragment.Args()) =
            Intent(context, EditorPageActivity::class.java).apply { putExtra(Tags.ARGS, args) }
    }

    private lateinit var injectionKey: String

    @Inject
    internal lateinit var fragmentBridge: EditorPageBridgeActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectionKey = savedInstanceState?.getString(INJECTION_KEY) ?: IdUtil.generateStringId()
        App.injections.get<EditorPageActivityComponent>(injectionKey).inject(this)

        setContentView(R.layout.activity_editor_page)
        findNavController(R.id.editorNavHost).setGraph(R.navigation.graph_editor_page, intent.extras)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(INJECTION_KEY, injectionKey)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        if(isFinishing) {
            App.injections.release<EditorPageActivityComponent>(injectionKey)
        }

        super.onDestroy()
    }

    override fun onBackPressed() {
        if(fragmentBridge.canCloseEditor()) {
            super.onBackPressed()
        }
    }
}
