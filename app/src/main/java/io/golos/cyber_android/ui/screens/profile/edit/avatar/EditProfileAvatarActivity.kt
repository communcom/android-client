package io.golos.cyber_android.ui.screens.profile.edit.avatar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags

class EditProfileAvatarActivity : AppCompatActivity() {
    companion object {
        fun getIntent(context: Context, args: EditProfileAvatarFragment.Args) =
            Intent(context, EditProfileAvatarActivity::class.java)
                .apply {
                    putExtra(Tags.ARGS,
                        context.serviceLocator.moshi.adapter(EditProfileAvatarFragment.Args::class.java)
                            .toJson(args))
                }
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

}
