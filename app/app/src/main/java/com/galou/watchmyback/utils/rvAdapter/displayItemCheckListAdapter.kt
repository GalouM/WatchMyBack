package com.galou.watchmyback.utils.rvAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.databinding.DetailsTripChecklistItemBinding

/**
 * @author galou
 * 2019-12-19
 */

class DisplayItemCheckListAdapter(
    var items: List<ItemCheckList>
) : RecyclerView.Adapter<DisplayItemCheckListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisplayItemCheckListViewHolder =
        DisplayItemCheckListViewHolder.from(
            parent
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DisplayItemCheckListViewHolder, position: Int) {
        holder.bindWithItem(items[position])
    }
}

class DisplayItemCheckListViewHolder(private val binding: DetailsTripChecklistItemBinding) : RecyclerView.ViewHolder(binding.root){
    fun bindWithItem(itemCheckList: ItemCheckList){
        with(binding){
            item = itemCheckList
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): DisplayItemCheckListViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DetailsTripChecklistItemBinding.inflate(layoutInflater, parent, false)
            return DisplayItemCheckListViewHolder(binding)
        }
    }
}