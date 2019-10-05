package io.golos.domain.mappers

import io.golos.commun4j.model.CyberDiscussion
import io.golos.domain.entities.*
import javax.inject.Inject

class CyberPostToEntityMapper
@Inject
constructor() :
    CommunToEntityMapper<CyberDiscussion, PostEntity> {

    @Suppress("CAST_NEVER_SUCCEEDS")
    override suspend fun map(communObject: CyberDiscussion): PostEntity {
        return PostEntity(
            DiscussionIdEntity(
                communObject.contentId.userId,
                communObject.contentId.permlink
            ),
            DiscussionAuthorEntity(
                CyberUser(communObject.author.userId.name ?: "unknown"),
                communObject.author.username ?: "unknown",
                communObject.author.avatarUrl ?: ""
            ),
            CommunityEntity(
                communObject.community.id,
                communObject.community.name!!,
                communObject.community.avatarUrl
            ),
            PostContent(
                communObject.content.attributes.title,
                ContentBody(
                    "{ \"id\": 1, \"type\": \"post\", \"attributes\": { \"version\": \"1.0\", \"title\": \"Сказка про царя\", \"type\": \"basic\" }, \"content\": [ { \"id\": 2, \"type\": \"paragraph\", \"content\": [ { \"id\": 3, \"type\": \"text\", \"content\": \"Много лет тому назад, \" }, { \"id\": 4, \"type\": \"mention\", \"content\": \"Царь\" }, { \"id\": 5, \"type\": \"text\", \"content\": \" купил себе айпад. \" }, { \"id\": 6, \"type\": \"tag\", \"content\": \"с_той_поры_прошли_века\" }, { \"id\": 7, \"type\": \"link\", \"content\": \" , Люди \", \"attributes\": { \"url\": \"https://www.youtube.com/watch?v=UiYlRkVxC_4\" } }, { \"id\": 8, \"type\": \"link\", \"content\": \"помнят \", \"attributes\": { \"url\": \"https://assets.pixyblog.com/wp-content/uploads/sites/3/2018/10/JULIE-LONDON-51-copy-515x600.jpg\" } }, { \"id\": 9, \"type\": \"link\", \"content\": \"чудака.\", \"attributes\": { \"url\": \"https://diletant.media\" } } ] }, { \"id\": 10, \"type\": \"image\", \"content\": \"https://assets.pixyblog.com/wp-content/uploads/sites/3/2018/10/JULIE-LONDON-51-copy-515x600.jpg\", \"attributes\": { \"description\": \"Hi!\" } } ] }"
                ),
                listOf()
            ),
            DiscussionVotes(
                communObject.votes.upCount > 0,
                communObject.votes.downCount > 0,
                communObject.votes.upCount,
                communObject.votes.downCount
            ),
            DiscussionCommentsCount(0L),            // note[AS] temporary zero - it'll be in a future
            DiscussionPayout(),
            DiscussionMetadata(communObject.meta.creationTime),
            DiscussionStats(
                0.toBigInteger(),
                0L
            )                  // note[AS] temporary zero - it'll be in a future
        )
    }
}