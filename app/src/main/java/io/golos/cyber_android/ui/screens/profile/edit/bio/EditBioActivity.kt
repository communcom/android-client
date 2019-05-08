package io.golos.cyber_android.ui.screens.profile.edit.bio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.base.BaseActivity

class EditBioActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context, args: EditBioFragment.Args) = Intent(context, EditBioActivity::class.java).apply {
            putExtra(Tags.ARGS,
                context.serviceLocator.moshi.adapter(EditBioFragment.Args::class.java)
                .toJson(args))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_bio_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container,
                    EditBioFragment.newInstance(intent.getStringExtra(Tags.ARGS))
                )
                .commitNow()
        }
    }

}
