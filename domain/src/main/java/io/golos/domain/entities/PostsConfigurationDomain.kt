package io.golos.domain.entities

data class PostsConfigurationDomain(
    val userId: String,
    val communityId: String?,
    val communityAlias: String?,
    val sortBy: SortByDomain,
    val timeFrame: TimeFrameDomain,
    val limit: Int,
    val offset: Int,
    val typeFeed: TypeFeedDomain,
    val allowNsfw: Boolean = false
) {

    enum class TypeFeedDomain {
        NEW,
        COMMUNITY,
        SUBSCRIPTIONS,
        BY_USER,
        TOP_LIKES,
        TOP_COMMENTS,
        TOP_REWARDS,
        HOT
    }

    enum class SortByDomain {
        TIME,
        TIME_DESC
    }

    enum class TimeFrameDomain {
        DAY,
        WEEK,
        MONTH,
        ALL
    }
}