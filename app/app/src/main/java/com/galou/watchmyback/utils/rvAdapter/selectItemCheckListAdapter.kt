package com.galou.watchmyback.utils.rvAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.databinding.AddTripChecklistItemBinding

/**
 * @author galou
 * 2019-12-03
 */

class SelectItemCheckListAdapter(
    var items: List<ItemCheckList>
) : RecyclerView.Adapter<ItemCheckListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCheckListViewHolder =
        ItemCheckListViewHolder.from(
            parent
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemCheckListViewHolder, position: Int) {
        holder.bindWithItem(items[position])
    }
}

class ItemCheckListViewHolder(private val binding: AddTripChecklistItemBinding) : RecyclerView.ViewHolder(binding.root){
    fun bindWithItem(itemCheckList: ItemCheckList){
        with(binding){
            item = itemCheckList
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): ItemCheckListViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = AddTripChecklistItemBinding.inflate(layoutInflater, parent, false)
            return ItemCheckListViewHolder(
                binding
            )
        }
    }
}