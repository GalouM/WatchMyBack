package com.galou.watchmyback.addTrip

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.databinding.AddTripChecklistItemBinding

/**
 * @author galou
 * 2019-12-03
 */

class ItemCheckListAdapter(
    var items: List<ItemCheckList>,
    private val viewModel: AddTripViewModel
) : RecyclerView.Adapter<ItemCheckListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCheckListViewHolder =
        ItemCheckListViewHolder.from(parent)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemCheckListViewHolder, position: Int) {
        holder.bindWithItem(viewModel, items[position])
    }
}

class ItemCheckListViewHolder(private val binding: AddTripChecklistItemBinding) : RecyclerView.ViewHolder(binding.root){
    fun bindWithItem(viewModel: AddTripViewModel, itemCheckList: ItemCheckList){
        with(binding){
            item = itemCheckList
            viewmodel = viewModel
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): ItemCheckListViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = AddTripChecklistItemBinding.inflate(layoutInflater, parent, false)
            return ItemCheckListViewHolder(binding)
        }
    }
}