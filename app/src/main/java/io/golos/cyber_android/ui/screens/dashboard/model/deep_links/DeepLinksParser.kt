package io.golos.cyber_android.ui.screens.dashboard.model.deep_links

import android.net.Uri
import io.golos.cyber_android.ui.screens.dashboard.dto.DeepLinkInfo

interface DeepLinksParser {
    suspend fun parse(uri: Uri): DeepLinkInfo?
}