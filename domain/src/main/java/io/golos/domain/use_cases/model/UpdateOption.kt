package io.golos.domain.use_cases.model

import io.golos.domain.Model

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
enum class UpdateOption : Model {
    REFRESH_FROM_BEGINNING,
    FETCH_NEXT_PAGE
}

enum class FeedTimeFrameOption {
    DAY,
    WEEK,
    MONTH,
    YEAR,
    ALL;
}