package io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view.view_commands

import android.net.Uri
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.domain.use_cases.model.DiscussionIdModel

class NavigateToImageViewCommand(val imageUri: Uri) : ViewCommand

class NavigateToLinkViewCommand(val link: Uri) : ViewCommand

class NavigateToUserProfileViewCommand(val userId: String) : ViewCommand

class NavigateToPostCommand(val discussionIdModel: DiscussionIdModel) : ViewCommand
