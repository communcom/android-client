package io.golos.data.persistence

import android.content.Context
import com.squareup.moshi.Moshi
import io.golos.data.dto.CommunityEntity
import io.golos.data.dto.CommunitySubscriptionsEntity
import io.golos.data.dto.FtueBoardStageEntity
import javax.inject.Inject

class PreferenceManagerImpl @Inject constructor(context: Context, private val moshi: Moshi) : PreferenceManager {

    private val preferences = context.getSharedPreferences("app", Context.MODE_PRIVATE)

    override fun saveFtueCommunitySubscriptions(communitySubscriptions: List<CommunityEntity>) {
        val communitySubscriptionsEntity = CommunitySubscriptionsEntity(communitySubscriptions)
        val adapter = moshi.adapter(CommunitySubscriptionsEntity::class.java)
        val json = adapter.toJson(communitySubscriptionsEntity)
        preferences.edit().putString(KEY_FTUE_COMMUNITY_SUBSCRIPTIONS, json).apply()
    }

    override fun getFtueCommunitySubscriptions(): List<CommunityEntity> {
        val communitySubscriptionsJson = preferences.getString(KEY_FTUE_COMMUNITY_SUBSCRIPTIONS, null)
        val adapter = moshi.adapter(CommunitySubscriptionsEntity::class.java)
        return communitySubscriptionsJson?.let { adapter.fromJson(it)?.communities } ?: listOf()
    }

    override suspend fun setFtueBoardStage(stage: FtueBoardStageEntity) {
        preferences.edit().putString(KEY_FTUE_BOARD_STAGE, stage.name).apply()
    }

    override suspend fun getFtueBoardStage(): FtueBoardStageEntity = FtueBoardStageEntity.valueOf(preferences.getString(KEY_FTUE_BOARD_STAGE, FtueBoardStageEntity.IDLE.name))

    private companion object{

        private const val KEY_FTUE_BOARD_STAGE = "KEY_FTUE_BOARD_STAGE"
        private const val KEY_FTUE_COMMUNITY_SUBSCRIPTIONS = "KEY_FTUE_COMMUNITY_SUBSCRIPTIONS"

    }
}