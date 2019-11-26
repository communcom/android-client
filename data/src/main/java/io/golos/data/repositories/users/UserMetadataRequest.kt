package io.golos.data.repositories.users

/**
 * Helper class to simplify call of "updateUserMetadata" method
 */
data class UserMetadataRequest(
    val avatarUrl: String?,
    val coverUrl: String?,
    val biography: String?,
    val facebook: String?,
    val telegram: String?,
    val whatsapp: String?,
    val wechat: String?,
    val bandWidthRequest: io.golos.commun4j.model.BandWidthRequest?,
    val clientAuthRequest: io.golos.commun4j.model.ClientAuthRequest?,
    val user: io.golos.commun4j.sharedmodel.CyberName,
    val key: String
)