package io.golos.cyber_android.ui.screens.profile.edit.avatar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.screens.profile.edit.cover.EditProfileCoverActivity

class EditProfileAvatarActivity : AppCompatActivity() {
    companion object {
        fun getIntent(context: Context, source: EditProfileCoverActivity.ImageSource) =
            Intent(context, EditProfileAvatarActivity::class.java)
                .apply {
                    putExtra(Tags.ARGS, source)
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile_avatar_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, EditProfileAvatarFragment.newInstance(intent.getSerializableExtra(Tags.ARGS) as EditProfileCoverActivity.ImageSource))
                .commitNow()
        }
    }

}
