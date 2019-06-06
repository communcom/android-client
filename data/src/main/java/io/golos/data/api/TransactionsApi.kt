package io.golos.data.api

import io.golos.cyber4j.services.model.ResultOk

interface TransactionsApi {
    fun waitForTransaction(transactionId: String): ResultOk
}