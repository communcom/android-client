package io.golos.data.mappers

import io.golos.commun4j.model.UserProfile
import io.golos.domain.dto.bc_profile.*

fun UserProfile.mapToBCProfileDomain(): BCProfileDomain =
    BCProfileDomain (
        accountName = account_name,
        headBlockNum = head_block_num,
        headBlockTime = head_block_time,
        privileged = isPrivileged,
        lastCodeUpdate = last_code_update,
        created = created,
        coreLiquidBalance = core_liquid_balance,
        ramQuota = ram_quota,
        netWeight = net_weight,
        cpuWeight = cpu_weight,
        ramUsage = ram_usage,
        permissions = permissions.map { it.mapToBCProfilePermissionDomain() }
    )

fun UserProfile.AccountPermission.mapToBCProfilePermissionDomain(): BCProfilePermissionDomain =
    BCProfilePermissionDomain(
        permName = perm_name,
        parent = parent,
        requiredAuth = required_auth.mapToBCProfileRequiredAuthDomain()
    )

fun UserProfile.AccountRequiredAuth.mapToBCProfileRequiredAuthDomain() =
    BCProfileRequiredAuthDomain (
        threshold = threshold,
        keys = keys.map { it.mapToBCProfileKeyDomain() },
        accounts = accounts.map { it.mapToBCProfileAuthDomain() }
    )

fun UserProfile.AccountKey.mapToBCProfileKeyDomain(): BCProfileKeyDomain =
    BCProfileKeyDomain(
        key = key,
        weight = weight
    )

fun UserProfile.AccountAuth.mapToBCProfileAuthDomain() : BCProfileAuthDomain =
    BCProfileAuthDomain(
        permission = permission.mapToBCProfileAuthPermissionDomain(),
        weight = weight
    )

fun UserProfile.AccountAuthPermission.mapToBCProfileAuthPermissionDomain(): BCProfileAuthPermissionDomain =
    BCProfileAuthPermissionDomain(
        actor = actor,
        permission = permission
    )