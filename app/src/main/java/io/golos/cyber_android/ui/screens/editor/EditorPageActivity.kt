package io.golos.cyber_android.ui.screens.editor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.base.BaseActivity

class EditorPageActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context) =
            Intent(context, EditorPageActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor_page)
    }
}
