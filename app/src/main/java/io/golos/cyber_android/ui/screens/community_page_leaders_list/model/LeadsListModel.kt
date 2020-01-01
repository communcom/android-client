package io.golos.cyber_android.ui.screens.community_page_leaders_list.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.community_page_leaders_list.dto.VoteResult
import io.golos.domain.dto.UserIdDomain

interface LeadsListModel: ModelBase {
    val items: LiveData<List<VersionedListItem>>

    suspend fun loadLeaders()

    suspend fun retry()

    suspend fun vote(leader: UserIdDomain): VoteResult

    suspend fun unvote(leader: UserIdDomain): VoteResult
}