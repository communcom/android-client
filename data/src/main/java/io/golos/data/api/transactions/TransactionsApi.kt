package io.golos.data.api.transactions

import io.golos.commun4j.services.model.ResultOk

interface TransactionsApi {
    fun waitForTransaction(transactionId: String): ResultOk
}