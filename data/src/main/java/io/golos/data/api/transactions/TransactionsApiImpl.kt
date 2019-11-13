package io.golos.data.api.transactions

import io.golos.commun4j.Commun4j
import io.golos.commun4j.services.model.ResultOk
import io.golos.data.api.Commun4jApiBase
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class TransactionsApiImpl
@Inject
constructor(
    commun4j: Commun4j,
    currentUserRepository: CurrentUserRepositoryRead,
    private val dispatchersProvider: DispatchersProvider
) : Commun4jApiBase(commun4j, currentUserRepository), TransactionsApi {

    override fun waitForTransaction(transactionId: String): ResultOk {
        //commun4j.waitForTransaction(transactionId).getOrThrow()
        return ResultOk("Ok")
    }
}