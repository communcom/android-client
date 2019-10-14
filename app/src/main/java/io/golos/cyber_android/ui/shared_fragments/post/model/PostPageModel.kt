package io.golos.cyber_android.ui.shared_fragments.post.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.domain.interactors.model.PostModel

interface PostPageModel : ModelBase {
    suspend fun getPost(): PostModel
}