package io.golos.cyber_android.ui.shared.extensions

import android.content.Intent
import android.net.Uri
import android.view.View

fun View.openLinkExternal(uri: Uri) = this.context.startActivity(Intent(Intent.ACTION_VIEW, uri))