package io.golos.cyber_android.ui.dialogs.select_community_dialog.dto

import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.CommunityDomain

class CommunitySelected(val community: CommunityDomain) : ViewCommand