package io.golos.data.persistence

import io.golos.data.dto.CommunityEntity
import io.golos.data.dto.FtueBoardStageEntity

interface PreferenceManager {

    /**
     * Mark that user pass ftue board
     */
    suspend fun setFtueBoardStage(stage: FtueBoardStageEntity)

    /**
     * Get ftue board stage
     *
     * @return ftue board stage
     */
    suspend fun getFtueBoardStage(): FtueBoardStageEntity

    fun saveFtueCommunitySubscriptions(communitySubscriptions: List<CommunityEntity>)

    fun getFtueCommunitySubscriptions(): List<CommunityEntity>

}