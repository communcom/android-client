package io.golos.cyber_android.ui.screens.wallet_convert.model.amount_calculator

import io.golos.data.repositories.wallet.WalletRepository
import io.golos.domain.dto.CommunityIdDomain
import timber.log.Timber
import javax.inject.Inject

class AmountCalculatorImpl
@Inject
constructor(
    private val walletRepository: WalletRepository
): AmountCalculator {
    override var sellAmount: Double? = null
    override var buyAmount: Double? = null

    private val rates = mutableMapOf<CommunityIdDomain, Double>()
    private lateinit var currentCommunityId: CommunityIdDomain

    /**
     * In sold coins
     */
    override val fee: Double = 0.0

    @Suppress("SuspiciousVarProperty")
    private var rate: Double = 1.0
        get() = if(rates.isEmpty()) 1.0 else rates[currentCommunityId]!!

    /**
     * false if we sell points and buy Commun
     */
    private var isInverted: Boolean = false

    /**
     * How many points is in one Commun
     */
    override val pointsInCommun: Double
        get() = 1 / rate

    /**
     * @return new value of buy amount or null in case of error
     */
    override fun updateSellAmount(value: String?): Boolean =
        updateAmount(value) {
            sellAmount = it
            buyAmount = it * getRateForCalculation()
        }

    /**
     * @return new value of sell amount or null in case of error
     */
    override fun updateBuyAmount(value: String?): Boolean =
        updateAmount(value) {
            buyAmount = it
            sellAmount = it * (1 / getRateForCalculation())
        }

    /**
     * [isInverted] false if we sell points and buy Commun
     */
    override suspend fun init(points: Double, communs: Double, isInverted: Boolean, communityId: CommunityIdDomain) {
        sellAmount = null
        buyAmount = null

        currentCommunityId = communityId

        rate = rates[currentCommunityId] ?: calculateRate(points, communs, communityId)

        this.isInverted = isInverted
    }

    private suspend fun calculateRate(points: Double, communs: Double, communityId: CommunityIdDomain): Double =
        (if(communs != 0.0 && points != 0.0) communs / points else (1 / walletRepository.getExchangeRate(communityId)))
            .also { rates[currentCommunityId] = it }

    override fun inverse() {
        isInverted = !isInverted

        val sellTemp = sellAmount
        sellAmount = buyAmount
        buyAmount = sellTemp
    }

    private fun updateAmount(value: String?, amountAction: (Double) -> Unit): Boolean {
        if(value.isNullOrBlank()) {
            sellAmount = null
            buyAmount = null
            return true
        }

        val calculatedAmount = try {
            value.toDouble()
        } catch (ex: NumberFormatException) {
            Timber.e(ex)
            sellAmount = null
            buyAmount = null
            return false
        }

        amountAction(calculatedAmount)

        return true
    }

    private fun getRateForCalculation(): Double =
        if(!isInverted) rate else 1 / rate
}