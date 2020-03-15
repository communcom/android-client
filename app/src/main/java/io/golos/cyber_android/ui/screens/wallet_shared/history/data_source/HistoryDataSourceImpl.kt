package io.golos.cyber_android.ui.screens.wallet_shared.history.data_source

import android.annotation.SuppressLint
import android.content.Context
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet_shared.getDisplayName
import io.golos.cyber_android.ui.screens.wallet_shared.history.dto.*
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.paging.LoadedItemsPagedListBase
import io.golos.cyber_android.ui.shared.utils.toDayShort
import io.golos.cyber_android.ui.shared.utils.toTimeShort
import io.golos.data.repositories.wallet.WalletRepository
import io.golos.domain.GlobalConstants
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.WalletTransferHistoryRecordDomain
import io.golos.utils.id.toLongId
import io.golos.utils.dates_local_now_calculator.DateCommonBase
import io.golos.utils.dates_local_now_calculator.ServerLocalNowDatesCalculator
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import io.golos.utils.helpers.capitalize

class HistoryDataSourceImpl
@Inject
constructor(
    private val appContext: Context,
    @Named(Clarification.PAGE_SIZE)
    private val pageSize: Int,
    private val walletRepository: WalletRepository
) : LoadedItemsPagedListBase<VersionedListItem>(pageSize),
    HistoryDataSource {

    private val datesCalculator = ServerLocalNowDatesCalculator()

    private var lastDateBase: DateCommonBase? = null
    private lateinit var lastSeparatorType: WalletHistorySeparatorType

    override var communityId: CommunityIdDomain = CommunityIdDomain(GlobalConstants.ALL_COMMUNITIES_CODE)

    override suspend fun getData(offset: Int): List<VersionedListItem>  {
        val serverData = walletRepository.getTransferHistory(offset, pageSize, communityId)

        val result = mutableListOf<VersionedListItem>()

        serverData.forEach { serverItem ->
            val serverDateBase = datesCalculator.calculateBase(serverItem.timeStamp)

            if(serverDateBase != lastDateBase) {
                val separator = getSeparator(datesCalculator.localNowBase, serverDateBase)

                lastSeparatorType = separator.type
                lastDateBase = serverDateBase

                result.add(separator)
            }

            val mappedItem = mapToTransferListItem(serverItem, lastSeparatorType)
            if(mappedItem == null) {
                Timber.w("Can't map wallet history item: $serverItem")
            } else {
                result.add(mappedItem)
            }
        }

        return result
    }

    private fun getSeparator(localNow: DateCommonBase, server: DateCommonBase) =
        when {
            localNow - server == 0 -> WalletHistorySeparatorListItem(
                type = WalletHistorySeparatorType.TODAY
            )

            localNow - server == 1 -> WalletHistorySeparatorListItem(
                type = WalletHistorySeparatorType.YESTERDAY
            )

            localNow - server < 30 ->
                WalletHistorySeparatorListItem(
                    type = WalletHistorySeparatorType.DAYS_AGO,
                    days = localNow - server
                )

            localNow - server < 60 -> WalletHistorySeparatorListItem(
                type = WalletHistorySeparatorType.MONTH_AGO
            )

            else -> WalletHistorySeparatorListItem(
                type = WalletHistorySeparatorType.PREVIOUSLY
            )
        }

    /**
     * @return null in case of some invalid data
     */
    @SuppressLint("DefaultLocale")
    private fun mapToTransferListItem(
        serverItem: WalletTransferHistoryRecordDomain,
        separatorType: WalletHistorySeparatorType
    ): VersionedListItem? {

        val serverActionType = serverItem.actionType.toUpperCase(Locale.getDefault())
        val serverTransferType = serverItem.transferType.toUpperCase(Locale.getDefault())

        val direction = when(serverItem.directionType.toUpperCase(Locale.getDefault())) {
            WalletHistoryConstants.DIRECTION_SEND -> WalletHistoryTransferDirection.SEND
            WalletHistoryConstants.DIRECTION_RECEIVE -> WalletHistoryTransferDirection.RECEIVE
             else -> return null
        }

        val transferType = when(serverActionType) {
            WalletHistoryConstants.ACTION_REWARD -> WalletHistoryTransferType.REWARD
            WalletHistoryConstants.ACTION_TRANSFER -> WalletHistoryTransferType.TRANSFER
            WalletHistoryConstants.ACTION_CONVERT -> WalletHistoryTransferType.CONVERT
            else -> {
                when(serverTransferType) {
                    WalletHistoryConstants.TRANSFER_TYPE_TRANSFER -> WalletHistoryTransferType.TRANSFER
                    WalletHistoryConstants.TRANSFER_TYPE_CONVERT -> WalletHistoryTransferType.CONVERT
                    else -> WalletHistoryTransferType.NONE
                }
            }
        }

        val displayName = when(serverActionType) {
            WalletHistoryConstants.ACTION_REWARD,
            WalletHistoryConstants.ACTION_UNHOLD -> serverItem.getDisplayName(appContext)

            WalletHistoryConstants.ACTION_TRANSFER ->
                when(direction) {
                    WalletHistoryTransferDirection.SEND -> serverItem.receiverName ?: serverItem.receiverId.userId
                    WalletHistoryTransferDirection.RECEIVE -> serverItem.senderName ?: serverItem.senderId.userId
                }

            WalletHistoryConstants.ACTION_CONVERT -> appContext.getString(R.string.refill)

            WalletHistoryConstants.ACTION_HOLD -> serverItem.holdType?.capitalize(Locale.getDefault()) ?: ""

            else -> return null
        }

        val timeStamp = when(separatorType) {
            WalletHistorySeparatorType.TODAY,
            WalletHistorySeparatorType.YESTERDAY,
            WalletHistorySeparatorType.DAYS_AGO -> serverItem.timeStamp.toTimeShort()
            else -> serverItem.timeStamp.toDayShort()
        }

        val mainIcon = when(serverActionType) {
            WalletHistoryConstants.ACTION_REWARD,
            WalletHistoryConstants.ACTION_UNHOLD -> serverItem.communityAvatarUrl

            WalletHistoryConstants.ACTION_TRANSFER ->
                when(direction) {
                    WalletHistoryTransferDirection.SEND -> serverItem.receiverAvatarUrl ?: WalletHistoryConstants.ICON_COMMUN
                    WalletHistoryTransferDirection.RECEIVE -> serverItem.senderAvatarUrl ?: WalletHistoryConstants.ICON_COMMUN
                }

            WalletHistoryConstants.ACTION_CONVERT ->
                if(serverTransferType == WalletHistoryConstants.TRANSFER_TYPE_TOKEN) {
                    serverItem.communityAvatarUrl
                } else {
                    WalletHistoryConstants.ICON_COMMUN
                }

            WalletHistoryConstants.ACTION_HOLD -> WalletHistoryConstants.ICON_LIKE
            else -> return null
        }

        val smallIcon = when(serverActionType) {
            WalletHistoryConstants.ACTION_REWARD,
            WalletHistoryConstants.ACTION_HOLD,
            WalletHistoryConstants.ACTION_UNHOLD -> ""

            WalletHistoryConstants.ACTION_TRANSFER -> WalletHistoryConstants.ICON_COMMUN

            WalletHistoryConstants.ACTION_CONVERT ->
                if(serverTransferType == WalletHistoryConstants.TRANSFER_TYPE_TOKEN) {
                    WalletHistoryConstants.ICON_COMMUN
                }
                else {
                    serverItem.communityAvatarUrl ?: WalletHistoryConstants.ICON_COMMUN
                }

            else -> return null
        }

        // See other fields

        return WalletHistoryTransferListItem(
            id = serverItem.id.toLongId(),

            mainIcon = mainIcon,
            smallIcon = smallIcon,
            isSmallIconVisible = smallIcon.isNotEmpty(),
            displayName = displayName,
            type = transferType,
            direction = direction,
            isOnHold = serverActionType == "HOLD",
            timeStamp = timeStamp,
            coinsQuantity = serverItem.coinsQuantity,
            coinsSymbol = serverItem.coinsSymbol
        )
    }
    override fun markAsFirst(item: VersionedListItem) = item

    override fun markAsLast(item: VersionedListItem) = item

    override fun unMarkAsLast(item: VersionedListItem) = item
}