package io.golos.domain.dto

data class PostsConfigurationDomain(
    val userId: String,
    val communityId: String?,
    val communityAlias: String?,
    val sortBy: SortByDomain = SortByDomain.TIME_DESC,
    val timeFrame: TimeFrameDomain = TimeFrameDomain.ALL,
    val limit: Int,
    val offset: Int,
    val typeFeed: TypeFeedDomain = TypeFeedDomain.BY_USER,
    val allowNsfw: Boolean = true
) {

    enum class TypeFeedDomain {
        NEW,
        COMMUNITY,
        SUBSCRIPTIONS,
        BY_USER,
        TOP_LIKES,
        TOP_COMMENTS,
        TOP_REWARDS,
        HOT,
        VOTED
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