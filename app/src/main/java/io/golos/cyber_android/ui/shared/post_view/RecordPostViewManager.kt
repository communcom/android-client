package io.golos.cyber_android.ui.shared.post_view

import io.golos.domain.dto.ContentIdDomain

interface RecordPostViewManager {
    fun onPostShow(postId: ContentIdDomain)

    fun onPostHide(postId: ContentIdDomain)
}