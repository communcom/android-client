package io.golos.cyber_android.ui.screens.profile.edit.avatar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.edit_profile_avatar_activity.EditProfileAvatarActivityComponent
import io.golos.cyber_android.ui.Tags

class EditProfileAvatarActivity : AppCompatActivity() {
    companion object {
        fun getIntent(context: Context, args: EditProfileAvatarFragment.Args) =
            Intent(context, EditProfileAvatarActivity::class.java).apply { putExtra(Tags.ARGS,args) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile_avatar_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, EditProfileAvatarFragment.newInstance(intent.getStringExtra(Tags.ARGS)))
                .commitNow()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if(isFinishing) {
            App.injections.release<EditProfileAvatarActivityComponent>()
        }
    }
}
