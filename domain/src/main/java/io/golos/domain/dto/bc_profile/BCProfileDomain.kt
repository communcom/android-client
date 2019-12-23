package io.golos.domain.dto.bc_profile

import java.util.*

data class BCProfileDomain(
    val accountName: String,
    val headBlockNum: Int,
    val headBlockTime: Date,
    val privileged: Boolean,
    val lastCodeUpdate: Date,
    val created: Date,
    val coreLiquidBalance: String?,
    val ramQuota: Long,
    val netWeight: Long,
    val cpuWeight: Long,
    val ramUsage: Long,
    val permissions: List<BCProfilePermissionDomain>
)