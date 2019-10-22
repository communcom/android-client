package io.golos.data.api.discussions

import android.util.Log
import io.golos.commun4j.Commun4j
import io.golos.commun4j.abi.implementation.comn.gallery.CreatemssgComnGalleryStruct
import io.golos.commun4j.abi.implementation.comn.gallery.DeletemssgComnGalleryStruct
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.model.*
import io.golos.commun4j.services.model.FeedSort
import io.golos.commun4j.services.model.FeedTimeFrame
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.api.Commun4jApiBase
import io.golos.data.api.communities.CommunitiesApi
import io.golos.data.repositories.current_user_repository.CurrentUserRepositoryRead
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.CommentDiscussionRaw
import io.golos.domain.commun_entities.CommunityId
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.commun_entities.PostDiscussionRaw
import io.golos.domain.interactors.model.DiscussionIdModel
import java.util.*
import javax.inject.Inject
import io.golos.commun4j.abi.implementation.comn.gallery.UpdatemssgComnGalleryStruct as UpdatemssgComnGalleryStruct1
import io.golos.commun4j.utils.Pair as CommunPair

class DiscussionsApiImpl
@Inject
constructor(
    commun4j: Commun4j,
    currentUserRepository: CurrentUserRepositoryRead,
    private val communitiesApi: CommunitiesApi,
    private val dispatchersProvider: DispatchersProvider
) : Commun4jApiBase(commun4j, currentUserRepository), DiscussionsApi {

    override fun createComment(
        body: String,
        parentAccount: CyberName,
        parentPermlink: Permlink,
        category: List<Tag>,
        metadata: DiscussionCreateMetadata,
        beneficiaries: List<Beneficiary>,
        vestPayment: Boolean,
        tokenProp: Long
    ): CommunPair<TransactionCommitted<CreatemssgComnGalleryStruct>, CreatemssgComnGalleryStruct> {
        // It's the BC method
        // We can wait for Yury or get Max's implementation from here:
        // https://github.com/communcom/communTestKit/blob/master/src/main/java/commun_test/communHelpers.java

        return StubDataFactory.createCommitedTransaction(StubDataFactory.getCreatemssgComnGalleryStruct("", ""))

//        return commun4j.createComment(
//            body,
//            parentAccount,
//            parentPermlink,
//            category,
//            metadata,
//            null,
//            beneficiaries,
//            vestPayment,
//            tokenProp.toShort(),
//            null,
//            BandWidthRequest(BandWidthSource.GOLOSIO_SERVICES)
//        )
//        .getOrThrow()
//        .run { this to this.extractResult() }
    }

    override fun createPost(
        title: String,
        body: String,
        tags: List<Tag>,
        communityId: CommunityId,
        metadata: DiscussionCreateMetadata,
        beneficiaries: List<Beneficiary>,
        vestPayment: Boolean,
        tokenProp: Long
    ): CommunPair<TransactionCommitted<CreatemssgComnGalleryStruct>, CreatemssgComnGalleryStruct> {
        // It's the BC method
        // We can wait for Yury or get Max's implementation from here:
        // https://github.com/communcom/communTestKit/blob/master/src/main/java/commun_test/communHelpers.java

        val community = communitiesApi.getCommunityById(communityId)
        val post = StubDataFactory.createPost(body, community!!, authState.user.name)
        Log.d("CREATE_POST", "createPost() UserId: ${post.contentId.userId}; permlink: ${post.contentId.permlink}")
        DataStorage.posts.add(post)

        val comments = CommentsDataFactory.createComments()
        DataStorage.comments[post.contentId.permlink] = comments

        return StubDataFactory.createCommitedTransaction(
            StubDataFactory.getCreatemssgComnGalleryStruct(post.contentId.userId, post.contentId.permlink))

//        return commun4j.createPost(
//            title,
//            body,
//            tags,
//            metadata,
//            null,
//            beneficiaries,
//            vestPayment,
//            tokenProp.toShort(),
//            null,
//            BandWidthRequest(BandWidthSource.GOLOSIO_SERVICES)
//        )
//        .getOrThrow()
//        .run { this to this.extractResult() }
    }

    override fun updatePost(
        postPermlink: Permlink,
        newTitle: String,
        newBody: String,
        newTags: List<Tag>,
        newJsonMetadata: DiscussionCreateMetadata
    ): CommunPair<TransactionCommitted<UpdatemssgComnGalleryStruct1>, UpdatemssgComnGalleryStruct1> {
        // It's the BC method
        // We can wait for Yury or get Max's implementation from here:
        // https://github.com/communcom/communTestKit/blob/master/src/main/java/commun_test/communHelpers.java
            val postIndex = DataStorage.posts.indexOfFirst { it.contentId.permlink == postPermlink.value }
            val post = DataStorage.posts[postIndex]
            DataStorage.posts[postIndex] = post.copy(content = newBody)

            return StubDataFactory.createCommitedTransaction(
                StubDataFactory.getUpdatemssgComnGalleryStruct(authState.user.name, postPermlink))

//        return commun4j.updatePost(postPermlink, newTitle, newBody, newTags, newJsonMetadata, BandWidthRequest(BandWidthSource.GOLOSIO_SERVICES))
//            .getOrThrow().run { this to this.extractResult() }
    }

    override fun deletePostOrComment(postOrCommentPermlink: Permlink):
            CommunPair<TransactionCommitted<DeletemssgComnGalleryStruct>, DeletemssgComnGalleryStruct> {
        // It's the BC method
        // We can wait for Yury or get Max's implementation from here:
        // https://github.com/communcom/communTestKit/blob/master/src/main/java/commun_test/communHelpers.java

            val postToRemove = DataStorage.posts.single { it.contentId.permlink == postOrCommentPermlink.value }
            DataStorage.posts.remove(postToRemove)

            return StubDataFactory.createCommitedTransaction(StubDataFactory.getDeletemssgComnGalleryStruct(authState.user.name, postOrCommentPermlink))

//        return commun4j.deletePostOrComment(postOrCommentPermlink, BandWidthRequest(BandWidthSource.GOLOSIO_SERVICES))
//            .getOrThrow().run {
//                this to this.extractResult()
//            }
    }

    override fun getCommunityPosts(
        communityId: String,
        limit: Int,
        sort: FeedSort,
        timeFrame: FeedTimeFrame,
        sequenceKey: String?,
        tags: List<String>?
    ): GetDiscussionsResultRaw {
        // note[AS] it'll be "getPosts" method in a future. So far we use a stub
        return GetDiscussionsResultRaw(listOf())
    }

    override fun getPost(user: CyberName, permlink: Permlink): PostDiscussionRaw {
        val post = DataStorage.posts.first {
            it.contentId.userId == user.name && it.contentId.permlink == permlink.value
        }
        Log.d("UPDATE_POST", "getPost() content: ${post.content}")
        return post

        // return commun4j.getPostRaw(user, "", permlink).getOrThrow()
    }

    override fun getUserSubscriptions(
        userId: String,
        limit: Int,
        sort: FeedSort,
        sequenceKey: String?
    ): GetDiscussionsResultRaw {
        // note[AS] no method so far. So we use a stub
        return GetDiscussionsResultRaw(listOf())
//        return commun4j.getUserSubscriptions(CyberName(userId), null, ContentParsingType.MOBILE, limit, sort, sequenceKey)
//            .getOrThrow()
    }

    override fun getUserPost(
        userId: String,
        limit: Int,
        sort: FeedSort,
        sequenceKey: String?
    ): GetDiscussionsResultRaw {
        // note[AS] it'll be "getPosts" method in a future. So far we use a stub
        Log.d("UPDATE_POST", "DiscussionsApiImpl::getUserPost()")
        return GetDiscussionsResultRaw(listOf())
        //return commun4j.getUserPosts(CyberName(userId), null, ContentParsingType.MOBILE, limit, sort, sequenceKey).getOrThrow()
    }

    override fun getCommentsOfPost(
        user: CyberName,
        permlink: Permlink,
        limit: Int,
        sort: FeedSort,
        sequenceKey: String?
    ): GetDiscussionsResultRaw {
        // note[AS] it'll be "getComments" method in a future. So far we use a stub
        return GetDiscussionsResultRaw(listOf())
//        return commun4j.getCommentsOfPost(
//            user,
//            null,
//            permlink,
//            ContentParsingType.MOBILE,
//            limit,
//            sort,
//            sequenceKey
//        ).getOrThrow()
    }

    override fun getComment(user: CyberName, permlink: Permlink): CommentDiscussionRaw {
        // note[AS] it'll be "getComment" method in a future. So far we use a stub
        return CommentDiscussionRaw(
            "",
            DiscussionVotes(0L, 0L),
            DiscussionMetadata(Date()),
            DiscussionId("", ""),
            DiscussionId("", ""),
            DiscussionAuthor(CyberName(""), "", ""),
            0L,
            listOf()
        )

        // return DiscussionsResult(listOf(), "")
        // return commun4j.getComment(user, null, permlink, ContentParsingType.MOBILE).getOrThrow()
    }

    /**
     * Returns list of comments
     * @param parentId - id of a post or a parent comment
     */
    override fun getCommentsList(offset: Int, pageSize: Int, parentId: DiscussionIdModel): List<CommentDiscussionRaw> =
        DataStorage.comments[parentId.permlink.value]!!
            .drop(offset)
            .take(pageSize)
}