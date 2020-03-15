package io.golos.data.api.discussions

import android.util.Log
import io.golos.commun4j.Commun4j
import io.golos.commun4j.abi.implementation.c.gallery.CreateCGalleryStruct
import io.golos.commun4j.abi.implementation.c.gallery.RemoveCGalleryStruct
import io.golos.commun4j.abi.implementation.c.gallery.UpdateCGalleryStruct
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.model.*
import io.golos.commun4j.services.model.FeedSort
import io.golos.commun4j.services.model.FeedTimeFrame
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.api.Commun4jApiBase
import io.golos.data.api.communities.CommunitiesApi
import io.golos.domain.commun_entities.CommentDiscussionRaw
import io.golos.domain.commun_entities.CommunityId
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.commun_entities.PostDiscussionRaw
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.use_cases.model.DiscussionAuthorModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import java.util.*
import javax.inject.Inject
import io.golos.commun4j.utils.Pair as CommunPair

class DiscussionsApiImpl
@Inject
constructor(
    commun4j: Commun4j,
    currentUserRepository: CurrentUserRepositoryRead,
    private val communitiesApi: CommunitiesApi
) : Commun4jApiBase(commun4j, currentUserRepository),
    DiscussionsApi {

    override fun createComment(
        body: String,
        parentAccount: CyberName,
        parentPermlink: Permlink,
        category: List<Tag>,
        metadata: DiscussionCreateMetadata,
        beneficiaries: List<Beneficiary>,
        vestPayment: Boolean,
        tokenProp: Long
    ): CommunPair<TransactionCommitted<CreateCGalleryStruct>, CreateCGalleryStruct> {
        // It's the BC method
        // We can wait for Yury or get Max's implementation from here:
        // https://github.com/communcom/communTestKit/blob/master/src/main/java/commun_test/communHelpers.java

        return PostsDataFactory.createCommitedTransaction(PostsDataFactory.getCreatemssgComnGalleryStruct("", ""))

//        return commun4j.sendComment(
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

    override fun createComment(
        commentContentAsJson: String,
        parentId: DiscussionIdModel,
        commentAuthor: DiscussionAuthorModel,
        commentPermlink: Permlink
    ): CommunPair<TransactionCommitted<CreateCGalleryStruct>, CreateCGalleryStruct> {
        val comment = CommentsDataFactory.createComment(commentContentAsJson, parentId, commentAuthor, commentPermlink)

        val parentPostPermlink = if(DataStorage.commentsForPost[parentId.permlink.value] != null) {  // Parent entity is a post
            parentId.permlink.value
        } else {                // Parent entity is a comment
            DataStorage.comments[parentId.permlink.value]!!.parentContentId!!.permlink
        }

        DataStorage.commentsForPost[parentPostPermlink]?.add(comment)
        DataStorage.comments[commentPermlink.value] = comment

        return PostsDataFactory.createCommitedTransaction(PostsDataFactory.getCreatemssgComnGalleryStruct("", ""))
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
    ): CommunPair<TransactionCommitted<CreateCGalleryStruct>, CreateCGalleryStruct> {
        // It's the BC method
        // We can wait for Yury or get Max's implementation from here:
        // https://github.com/communcom/communTestKit/blob/master/src/main/java/commun_test/communHelpers.java

        val community = communitiesApi.getCommunityById(CommunityIdDomain(communityId.id))

        val postPermlink = Permlink.generate()

        val comments = CommentsDataFactory.createComments(postPermlink, authState.user.userId)

        val post = PostsDataFactory.createPost(body, community!!, authState.user.userId, comments.size, postPermlink)
        Log.d("CREATE_POST", "createPost() UserId: ${post.contentId.userId}; permlink: ${post.contentId.permlink}")
        DataStorage.posts.add(post)

        DataStorage.commentsForPost[post.contentId.permlink] = comments

        comments.forEach {
            DataStorage.comments[it.contentId.permlink] = it

            it.child.forEach {
                DataStorage.comments[it.contentId.permlink] = it
            }
        }

        return PostsDataFactory.createCommitedTransaction(
            PostsDataFactory.getCreatemssgComnGalleryStruct(post.contentId.userId.name, post.contentId.permlink)
        )

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
    ): CommunPair<TransactionCommitted<UpdateCGalleryStruct>, UpdateCGalleryStruct> {
        // It's the BC method
        // We can wait for Yury or get Max's implementation from here:
        // https://github.com/communcom/communTestKit/blob/master/src/main/java/commun_test/communHelpers.java
        val postIndex = DataStorage.posts.indexOfFirst { it.contentId.permlink == postPermlink.value }
        val post = DataStorage.posts[postIndex]
        DataStorage.posts[postIndex] = post.copy(content = newBody)

        return PostsDataFactory.createCommitedTransaction(
            PostsDataFactory.getUpdatemssgComnGalleryStruct(authState.user.userId, postPermlink)
        )

//        return commun4j.updatePost(postPermlink, newTitle, newBody, newTags, newJsonMetadata, BandWidthRequest(BandWidthSource.GOLOSIO_SERVICES))
//            .getOrThrow().run { this to this.extractResult() }
    }

    override fun deletePost(postPermlink: Permlink):
            CommunPair<TransactionCommitted<RemoveCGalleryStruct>, RemoveCGalleryStruct> {
        // It's the BC method
        // We can wait for Yury or get Max's implementation from here:
        // https://github.com/communcom/communTestKit/blob/master/src/main/java/commun_test/communHelpers.java

        val postToRemove = DataStorage.posts.single { it.contentId.permlink == postPermlink.value }
        DataStorage.posts.remove(postToRemove)

        return PostsDataFactory.createCommitedTransaction(
            PostsDataFactory.getDeletemssgComnGalleryStruct(
                authState.user.userId,
                postPermlink
            )
        )

//        return commun4j.deletePostOrComment(postOrCommentPermlink, BandWidthRequest(BandWidthSource.GOLOSIO_SERVICES))
//            .getOrThrow().run {
//                this to this.extractResult()
//            }
    }

    override fun deleteComment(commentPermlink: Permlink):
            CommunPair<TransactionCommitted<RemoveCGalleryStruct>, RemoveCGalleryStruct> {

        return PostsDataFactory.createCommitedTransaction(
            PostsDataFactory.getDeletemssgComnGalleryStruct(
                authState.user.userId,
                commentPermlink
            )
        )
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
            it.contentId.userId.name == user.name && it.contentId.permlink == permlink.value
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
            DiscussionVotes(0L, 0L, false, false),
            DiscussionMetadata(Date()),
            DiscussionId(CyberName(""), "", ""),
            DiscussionId(CyberName(""), "", ""),
            DiscussionAuthor(CyberName(""), "", ""),
            0L,
            listOf()
        )

        // return DiscussionsResult(listOf(), "")
        // return commun4j.getComment(user, null, permlink, ContentParsingType.MOBILE).getOrThrow()
    }
}