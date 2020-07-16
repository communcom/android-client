package io.golos.cyber_android.ui.screens.post_edit.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.post_edit.activity.di.EditorPageActivityComponent
import io.golos.cyber_android.ui.screens.post_edit.fragment.view.EditorPageFragment
import io.golos.cyber_android.ui.screens.post_edit.shared.EditorPageBridgeActivity
import io.golos.cyber_android.ui.shared.Tags
import io.golos.cyber_android.ui.shared.base.ActivityBase
import javax.inject.Inject

class EditorPageActivity : ActivityBase() {
    companion object {
        fun getIntent(context: Context, args: EditorPageFragment.Args = EditorPageFragment.Args()) =
            Intent(context, EditorPageActivity::class.java).apply { putExtra(Tags.ARGS, args) }
    }

    @Inject
    internal lateinit var fragmentBridge: EditorPageBridgeActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_editor_page)

        if (!intent.hasExtra(Tags.ARGS)) {
            val args = EditorPageFragment.Args().apply {
                if (intent?.action == Intent.ACTION_SEND) {
                    if ("text/plain" == intent.type) {
                        sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                    } else if (intent.type?.startsWith("image/") == true) {
                        sharedImage = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri?
                    }
                }
            }
            intent.putExtra(Tags.ARGS, args)
        }

        findNavController(R.id.editorNavHost).setGraph(R.navigation.graph_editor_page, intent.extras)
    }

    override fun inject(key: String) = App.injections.get<EditorPageActivityComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<EditorPageActivityComponent>(key)

    override fun onBackPressed() {
        if(fragmentBridge.canCloseEditor()) {
            super.onBackPressed()
        }
    }
}