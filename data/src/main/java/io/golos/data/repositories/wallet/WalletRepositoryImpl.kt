package io.golos.data.repositories.wallet

import io.golos.commun4j.Commun4j
import io.golos.commun4j.model.BandWidthRequest
import io.golos.commun4j.services.model.DonationPostModel
import io.golos.commun4j.services.model.WalletQuantity
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbol
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import io.golos.data.mappers.mapToDonationsDomain
import io.golos.data.mappers.mapToWalletCommunityBalanceRecordDomain
import io.golos.data.mappers.mapToWalletTransferHistoryRecordDomain
import io.golos.data.repositories.network_call.NetworkCallProxy
import io.golos.domain.GlobalConstants
import io.golos.domain.UserKeyStore
import io.golos.domain.dto.*
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.utils.format.DoubleFormatter
import timber.log.Timber
import javax.inject.Inject

class WalletRepositoryImpl
@Inject
constructor(
    private val callProxy: NetworkCallProxy,
    private val currentUserRepository: CurrentUserRepository,
    private val commun4j: Commun4j,
    private val userKeyStore: UserKeyStore
) : WalletRepository {

    override suspend fun getBalance(): List<WalletCommunityBalanceRecordDomain> {
        val callResult = callProxy.call {
            Timber.tag("NET_SOCKET").d("WalletRepositoryImpl::getBalance()")
            commun4j.getBalance(CyberName(currentUserRepository.userId.userId))
        }

        if(callResult.all { it.symbol.value != GlobalConstants.COMMUN_CODE }) {
            callProxy.callBC {
                commun4j.openBalance(
                    symbol = CyberSymbol("4,CMN"),
                    ramPayer = CyberName(currentUserRepository.userId.userId),
                    owner = CyberName(currentUserRepository.userId.userId),
                    bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                    key = userKeyStore.getKey(UserKeyType.ACTIVE)
                )
            }
        }

        return callResult.balances.map { it.mapToWalletCommunityBalanceRecordDomain() }
    }

    /**
     * Returns a quantity of the community points in one commun
     */
    override suspend fun getExchangeRate(communityId: CommunityIdDomain): Double =
        callProxy.call {
            commun4j.getBuyPrice(
                pointSymbol = CyberSymbolCode(communityId.code),
                quantity = WalletQuantity("1 ${GlobalConstants.COMMUN_CODE}"))
        }.price.quantity

    override suspend fun getTransferHistory(offset: Int, limit: Int, communityId: CommunityIdDomain,historyFilterDomain: HistoryFilterDomain?): List<WalletTransferHistoryRecordDomain> =
        callProxy.call {commun4j.getTransferHistory(
            symbol = CyberSymbolCode(communityId.code),
            offset = offset,
            userId = CyberName(currentUserRepository.userId.userId),
            rewards = historyFilterDomain?.reward,
            claim = historyFilterDomain?.claim ,
            transferType = historyFilterDomain?.transferType,
            holdType = historyFilterDomain?.holdType,
            limit = limit,
            direction = historyFilterDomain?.direction,
            donation = historyFilterDomain?.donation
        )}
            .items.map { it.mapToWalletTransferHistoryRecordDomain() }

    override suspend fun sendToUser(toUser: UserIdDomain, amount: Double, communityId: CommunityIdDomain) =
        sendToUser(toUser, amount, communityId, "")

    override suspend fun donate(contentId: ContentIdDomain, amount: Double, communityId: CommunityIdDomain): DonationsDomain? {
        Timber.tag("NET_SOCKET").d("WalletRepositoryImpl::donate(postId: $contentId)")
        sendToUser(
            toUser = contentId.userId,
            amount = amount,
            communityId = communityId,
            memo = "donation for ${contentId.communityId.code}:${contentId.userId.userId}:${contentId.permlink}")

        val donationQuery = listOf(DonationPostModel(contentId.userId.userId, contentId.permlink))
        return callProxy.call { commun4j.getDonations(donationQuery) }.items.firstOrNull()?.mapToDonationsDomain()
    }

    override suspend fun convertPointsToCommun(amount: Double, communityId: CommunityIdDomain) {
        val amountAsString = DoubleFormatter.formatToServerPoints(amount)

        callProxy.callBC {
            commun4j.transfer(
                to = CyberName(GlobalConstants.C_POINT_USER_ID),
                amount = amountAsString,
                currency = communityId.code,
                memo = "$amountAsString $communityId",
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                key = userKeyStore.getKey(UserKeyType.ACTIVE),
                from = CyberName(currentUserRepository.userId.userId)
            )
        }
            .let {
                callProxy.call { commun4j.waitForTransaction(it.transaction_id) }
            }
    }

    override suspend fun convertCommunToPoints(amount: Double, communityId: CommunityIdDomain) {
        callProxy.callBC {
            commun4j.exchange(
                to = CyberName(GlobalConstants.C_POINT_USER_ID),
                amount = DoubleFormatter.formatToServerTokens(amount),
                currency = GlobalConstants.COMMUN_CODE,
                memo = communityId.code,
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                key = userKeyStore.getKey(UserKeyType.ACTIVE),
                from = CyberName(currentUserRepository.userId.userId)
            )
        }
            .let {
                callProxy.call { commun4j.waitForTransaction(it.transaction_id) }
            }
    }

    override suspend fun getDonations(postId: ContentIdDomain): DonationsDomain? {
        val donationQuery = listOf(DonationPostModel(postId.userId.userId, postId.permlink))
        return callProxy.call { commun4j.getDonations(donationQuery) }.items.firstOrNull()?.mapToDonationsDomain()
    }

    private suspend fun sendToUser(toUser: UserIdDomain, amount: Double, communityId: CommunityIdDomain, memo: String) {
        Timber.tag("NET_SOCKET").d("WalletRepositoryImpl::sendToUser(memo: $memo)")
        if(communityId.code != GlobalConstants.COMMUN_CODE) {
            callProxy.callBC {
                commun4j.transfer(
                    to = CyberName(toUser.userId),
                    amount = DoubleFormatter.formatToServerPoints(amount),
                    currency = communityId.code,
                    memo = memo,
                    bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                    key = userKeyStore.getKey(UserKeyType.ACTIVE),
                    from = CyberName(currentUserRepository.userId.userId)
                )
            }
                .let {
                    callProxy.call { commun4j.waitForTransaction(it.transaction_id) }
                }
        } else {
            callProxy.callBC {
                commun4j.exchange(
                    to = CyberName(toUser.userId),
                    amount = DoubleFormatter.formatToServerTokens(amount),
                    currency = communityId.code,
                    memo = memo,
                    bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                    key = userKeyStore.getKey(UserKeyType.ACTIVE),
                    from = CyberName(currentUserRepository.userId.userId)
                )
            }
                .let {
                    callProxy.call { commun4j.waitForTransaction(it.transaction_id) }
                }
        }
    }
}