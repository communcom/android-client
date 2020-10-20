package io.golos.cyber_android.ui.shared.recycler_view.versioned

import io.golos.cyber_android.ui.shared.recycler_view.DiffAlgBase

class VersionedDiffAlg(
    oldList: List<VersionedListItem>,
    newList: List<VersionedListItem>
) : DiffAlgBase<VersionedListItem>(oldList, newList) {
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].version == newList[newItemPosition].version
}