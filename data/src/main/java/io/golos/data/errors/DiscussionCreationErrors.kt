package io.golos.data.errors

class CannotDeleteDiscussionWithChildCommentsException(cause: Throwable?): IllegalStateException() {
    override val message = cause?.message
}