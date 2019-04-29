package io.golos.cyber_android.ui.screens.profile.edit.cover

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.base.BaseActivity

class EditProfileCoverActivity : BaseActivity() {

    enum class ImageSource {
        CAMERA, GALLERY
    }

    companion object {
        fun getIntent(context: Context, source: ImageSource) =
            Intent(context, EditProfileCoverActivity::class.java)
                .apply {
                    putExtra(Tags.ARGS, source)
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile_cover_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, EditProfileCoverFragment.newInstance(intent.getSerializableExtra(Tags.ARGS) as ImageSource))
                .commitNow()
        }
    }

}
