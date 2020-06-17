package io.golos.cyber_android.ui.screens.post_view.dto

import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.ContentBlock

@Deprecated("")
class StartEditPostViewCommand(val postId: ContentIdDomain) : ViewCommand

class NavigationToEditPostViewCommand(val contentId: ContentIdDomain) : ViewCommand

class NavigateToEditComment(val contentId: ContentIdDomain, val body: ContentBlock?): ViewCommand

class NavigationToPostMenuViewCommand(val postMenu: PostMenu) : ViewCommand

class SharePostCommand(val shareUrl: String) : ViewCommand

class ReportPostCommand(val contentId: ContentIdDomain) : ViewCommand

class DeletePostCommand : ViewCommand

class ShowCommentsSortingMenuViewCommand : ViewCommand

class ClearCommentInputCommand : ViewCommand

class ShowCommentMenuViewCommand(val commentId: DiscussionIdModel) : ViewCommand

class NavigateToReplyCommentViewCommand(val contentId: ContentIdDomain, val body: ContentBlock?): ViewCommand