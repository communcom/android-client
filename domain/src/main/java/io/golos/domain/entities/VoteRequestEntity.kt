package io.golos.domain.entities

import io.golos.domain.Entity
import io.golos.domain.requestmodel.Identifiable

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-21.
 */
sealed class VoteRequestEntity(val power: Short, val discussionIdEntity: DiscussionIdEntity) : Entity, Identifiable {
    init {
        if (power > 10_000) throw IllegalArgumentException("vote range exceed, now $power")
        if (power < -10_000) throw IllegalArgumentException("vote range to low, now $power")
    }

    class VoteForAPostRequestEntity(power: Short, discussionIdEntity: DiscussionIdEntity) :
        VoteRequestEntity(power, discussionIdEntity)

    class VoteForACommentRequestEntity(power: Short, discussionIdEntity: DiscussionIdEntity) :
        VoteRequestEntity(power, discussionIdEntity)

    inner class Id(val discussionIdEntity: DiscussionIdEntity) : Identifiable.Id() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            if (discussionIdEntity != other.discussionIdEntity) return false

            return true
        }

        override fun hashCode(): Int {
            return discussionIdEntity.hashCode()
        }
    }

    override val id: Identifiable.Id
            by lazy { Id(discussionIdEntity) }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VoteRequestEntity

        if (power != other.power) return false
        if (discussionIdEntity != other.discussionIdEntity) return false

        return true
    }


    override fun hashCode(): Int {
        var result = power.toInt()
        result = 31 * result + discussionIdEntity.hashCode()
        return result
    }

    override fun toString(): String {
        return "VoteRequestEntity(power=$power, discussionIdEntity=$discussionIdEntity)"
    }
}