package io.golos.data.mappers

import io.golos.domain.dto.FtueBoardStageDomain
import io.golos.domain.dto.FtueBoardStageEntity

fun FtueBoardStageEntity.mapToFtueBoardStageDomain(): FtueBoardStageDomain {
    return when(this){
        FtueBoardStageEntity.IDLE -> FtueBoardStageDomain.IDLE
        FtueBoardStageEntity.NEED_SHOW -> FtueBoardStageDomain.NEED_SHOW
        FtueBoardStageEntity.SEARCH_COMMUNITIES -> FtueBoardStageDomain.SEARCH_COMMUNITIES
        FtueBoardStageEntity.PASSED -> FtueBoardStageDomain.PASSED
        FtueBoardStageEntity.FINISH -> FtueBoardStageDomain.FINISH
        else -> FtueBoardStageDomain.IDLE
    }
}

fun FtueBoardStageDomain.mapToFtueBoardStageEntity(): FtueBoardStageEntity {
    return when(this){
        FtueBoardStageDomain.IDLE -> FtueBoardStageEntity.IDLE
        FtueBoardStageDomain.NEED_SHOW -> FtueBoardStageEntity.NEED_SHOW
        FtueBoardStageDomain.SEARCH_COMMUNITIES -> FtueBoardStageEntity.SEARCH_COMMUNITIES
        FtueBoardStageDomain.PASSED -> FtueBoardStageEntity.PASSED
        FtueBoardStageDomain.FINISH -> FtueBoardStageEntity.FINISH
        else -> FtueBoardStageEntity.IDLE
    }
}