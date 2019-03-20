package io.golos.cyber_android.ui.dialogs.sort

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.widgets.sorting.SortingType

class SortsAdapter(private val values: Array<SortingType>, private val listener: ((SortingType) -> Unit)): RecyclerView.Adapter<SortsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sorts, parent, false) as TextView
        return ViewHolder(view)
    }

    override fun getItemCount() = values.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sorting = values[position]
        holder.textView.setText(sorting.title)
        holder.textView.setOnClickListener {
            listener.invoke(sorting)
        }
    }



    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
}