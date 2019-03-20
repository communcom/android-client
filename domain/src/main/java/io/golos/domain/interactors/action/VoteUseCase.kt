package io.golos.domain.interactors.action

import androidx.lifecycle.LiveData
import io.golos.cyber4j.model.DiscussionId
import io.golos.domain.interactors.UseCase
import io.golos.domain.model.QueryResult

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-20.
 */
class VoteUseCase : UseCase<Map<DiscussionId, QueryResult<DiscussionId>>> {

    override val getAsLiveData: LiveData<Map<DiscussionId, QueryResult<DiscussionId>>>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
}