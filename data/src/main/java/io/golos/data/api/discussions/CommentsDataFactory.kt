package io.golos.data.api.discussions

import io.golos.commun4j.model.DiscussionAuthor
import io.golos.commun4j.model.DiscussionId
import io.golos.commun4j.model.DiscussionMetadata
import io.golos.commun4j.model.DiscussionVotes
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.toCyberName
import io.golos.domain.commun_entities.CommentDiscussionRaw
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.use_cases.model.DiscussionAuthorModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.posts_parsing_rendering.mappers.comment_to_json.CommentToJsonMapper
import java.util.*
import kotlin.random.Random

object CommentsDataFactory {
    private val texts = listOf(
        "Far over the misty mountains cold",
        "To dungeons deep and caverns old",
        "We must away, ere break of day,",
        "To find our long-forgotten gold.",
        "The dwarves of yore made mighty spells,",
        "While hammers fell like ringing bells",
        "In places deep, where dark things sleep,",
        "In hollow halls beneath the fells.",
        "In a hole in the ground there lived a hobbit. Not a nasty, dirty, wet hole, filled with the ends of worms and an oozy smell, nor yet a dry, bare, sandy hole with nothing in it to sit down on or to eat: it was a hobbit-hole, and that means comfort.",
        "It had a perfectly round door like a porthole, painted green, with a shiny yellow brass knob in the exact middle.",
        "The door opened on to a tube-shaped hall like a tunnel: a very comfortable tunnel without smoke, with panelled walls, and floors tiled and carpeted, provided with polished chairs,",
        "This hobbit was a very well-to-do hobbit, and his name was Baggins.",
        "The Bagginses had lived in the neighbourhood of The Hill for time out of mind, and people considered them very respectable, not only because most of them were rich, but also because they never had any adventures or did anything unexpected: you could tell what a Baggins would say",
        "The mother of our particular hobbit... what is a hobbit? I suppose hobbits need some description nowadays, since they have become rare and shy of the Big People, as they call us. \nThey are (or were) a little people, about half our height, and smaller than the bearded Dwarves. \nHobbits have no beards. There is little or no magic about them, except the ordinary",
        "everyday sort which helps them to disappear quietly and quickly when large stupid folk like you and me come blundering along, making a noise like elephants which they can hear a mile off. \nThey are inclined to be at in the stomach; they dress in bright colours (chiefly green and yellow); wear no shoes, because their feet grow natural leathery soles and thick",
        "warm brown hair like the stuff on their heads (which is curly); have long clever brown fingers, good-natured faces, and laugh deep fruity laughs (especially after dinner, which they have twice a day when they can get it). \nNow you know enough to go on with. As I was saying, the mother of this hobbit - of Bilbo Baggins, that is - was the fabulous Belladonna Took,",
        "one of the three remarkable daughters of the Old Took, head of the hobbits who lived across The Water, the small river that ran at the foot of The Hill. \nIt was often said (in other families) that long ago one of the Took ancestors must have taken a fairy wife. That was, of course, absurd, but certainly there was still something not entirely hobbit-like about them"
    )

    private val users = listOf(
        DiscussionAuthor(CyberName("tst3xxiihfzq"), "johnston-yaeko-i", "https://pickaface.net/gallery/avatar/centurypixel5229a9f0ae77f.png"),   // It's me
        DiscussionAuthor(CyberName("tst3fkdlkfdl"), "archibald", "https://pickaface.net/gallery/avatar/JustAJok3r54fe12cb4f2f3.png"),
        DiscussionAuthor(CyberName("tst3idfkldfk"), "ann-nikolet", "https://pickaface.net/gallery/avatar/20130407_010558_2673_Kim.png"),
        DiscussionAuthor(CyberName("tst3fopetidl"), "saturas", "https://pickaface.net/gallery/avatar/unr_yourmom_170930_0712_9sdfci.png"),
        DiscussionAuthor(CyberName("tst3fdkllkcz"), "elena", "https://pickaface.net/gallery/avatar/ThythyRock52f7b5c2c531c.png"),
        DiscussionAuthor(CyberName("tst3lkdjfglf"), "alex", "https://pickaface.net/gallery/avatar/20130205_145636_2557_will.png"),
        DiscussionAuthor(CyberName("tst3kflrglak"), "nadia", "https://pickaface.net/gallery/avatar/cobrafox225379accecc127.png"),

        DiscussionAuthor(CyberName("tst3xxiihfzq"), "trinixy", null),
        DiscussionAuthor(CyberName("tst3xxiihfzq"), "james", null),
        DiscussionAuthor(CyberName("tst3xxiihfzq"), "berty", null)
    )

    fun createComments(postPermlink: Permlink, currentUserId: String): MutableList<CommentDiscussionRaw> {
        val firstLevelsCount = Random.nextInt(30, 50)
        val firstLevelList = mutableListOf<CommentDiscussionRaw>()

        for(i in 0 until firstLevelsCount) {
            val firstLevelComment = createRandomComment(DiscussionId(currentUserId.toCyberName(), "communityId", postPermlink.value))

            val secondLevelList = mutableListOf<CommentDiscussionRaw>()

            if(Random.nextInt() % 3 == 0) {         // Second level comments
                val secondLevelsCount = Random.nextInt(3, 50)

                var parentSecondLevelId: DiscussionId? = null
                for (j in 0 until secondLevelsCount) {
                    val parentId = if (j > 0 && j % 5 == 0) {
                        parentSecondLevelId!!
                    } else {
                        firstLevelComment.contentId
                    }

                    val secondLevelComment = createRandomComment(parentId)
                    secondLevelList.add(secondLevelComment)

                    if (j == 0) {
                        parentSecondLevelId = secondLevelComment.contentId
                    }
                }
            }

            firstLevelList.add(firstLevelComment.copy(childTotal = secondLevelList.size.toLong(), child = secondLevelList))
        }

        return firstLevelList
    }

    fun createComment(
        contentAsJson: String,
        post: DiscussionIdModel,
        author: DiscussionAuthorModel,
        permlink: Permlink): CommentDiscussionRaw {
        val cyberName = CyberName(author.userId.userId)
        val communityId = UUID.randomUUID().toString()
        return CommentDiscussionRaw(
            content = contentAsJson,
            votes = DiscussionVotes(Random.nextLong(-1000, 1000), Random.nextLong(-1000, 1000)),
            meta = DiscussionMetadata(Date()),
            contentId = DiscussionId(cyberName, communityId, permlink.value),
            author = DiscussionAuthor(cyberName, author.username, author.avatarUrl),
            parentContentId = DiscussionId(cyberName, communityId, post.permlink.value),
            childTotal = 0L,
            child = listOf()
        )
    }

    private fun createRandomComment(parentId: DiscussionId?): CommentDiscussionRaw {
        val communityId = UUID.randomUUID().toString()
        val content = CommentToJsonMapper.mapTextToJson(texts[Random.nextInt(texts.size)])
        val votes = DiscussionVotes(Random.nextLong(-1000, 1000), Random.nextLong(-1000, 1000))
        val meta = DiscussionMetadata(Random.nextDate())
        val author = users[Random.nextInt(users.size)]
        val contentId = DiscussionId(author.userId, communityId, Permlink.generate().value)

        return CommentDiscussionRaw(
            content = content,
            votes = votes,
            meta = meta,
            contentId = contentId,
            author = author,
            parentContentId = parentId,
            childTotal = 0L,
            child = listOf()
        )
    }

    private fun Random.nextDate() = Date(Date().time-this.nextLong(2_592_000_000))            // in 1 month
}