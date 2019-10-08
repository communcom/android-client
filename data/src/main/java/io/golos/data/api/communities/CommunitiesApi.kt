package io.golos.data.api.communities

import io.golos.domain.commun_entities.Community

interface CommunitiesApi {
    fun getCommunitiesList(offset: Int, pageSize: Int): List<Community>
}