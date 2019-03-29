package io.golos.cyber_android.ui.common

import androidx.recyclerview.widget.RecyclerView
import io.golos.domain.interactors.model.DiscussionModel

abstract class AbstractDiscussionModelAdapter<M: DiscussionModel>
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    abstract fun submit(list: List<M>)

    abstract var isLoading: Boolean
}