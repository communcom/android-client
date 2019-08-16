package io.golos.cyber_android.ui.screens.profile.edit.cover

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.edit_profile_cover_activity.EditProfileCoverActivityComponent
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.base.ActivityBase

class EditProfileCoverActivity : ActivityBase() {

    companion object {
        fun getIntent(context: Context, args: EditProfileCoverFragment.Args) =
            Intent(context, EditProfileCoverActivity::class.java).apply { putExtra(Tags.ARGS, args) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile_cover_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, EditProfileCoverFragment.newInstance(intent.getStringExtra(Tags.ARGS)))
                .commitNow()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if(isFinishing) {
            App.injections.release<EditProfileCoverActivityComponent>()
        }
    }
}
