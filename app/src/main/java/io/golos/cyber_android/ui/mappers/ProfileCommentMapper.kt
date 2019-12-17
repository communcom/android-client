package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Comment
import io.golos.cyber_android.ui.screens.profile_comment_page_menu.model.CommentMenu

fun Comment.mapToCommentMenu(): CommentMenu = CommentMenu(
    contentId = contentId,
    communityId = community.communityId,
    communityName = community.name,
    communityAvatarUrl = community.avatarUrl,
    creationTime = meta.creationTime,
    authorUserId = author.userId,
    authorUsername = author.username,
    permlink = contentId.permlink
)