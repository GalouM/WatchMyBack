package com.galou.watchmyback.addModifyCheckList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.databinding.AddModifyChecklistRvItemBinding

/**
 * @author galou
 * 2019-11-23
 */

class ModifyItemAdapter(
    var items: List<ItemCheckList>,
    val viewModel: AddModifyCheckListViewModel
) :RecyclerView.Adapter<ModifyItemViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModifyItemViewHolder =
        ModifyItemViewHolder.from(parent)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ModifyItemViewHolder, position: Int) {
        holder.bindWithItem(viewModel, items[position])
    }
}

class ModifyItemViewHolder(private val binding: AddModifyChecklistRvItemBinding) : RecyclerView.ViewHolder(binding.root){

    fun bindWithItem(viewModel: AddModifyCheckListViewModel, itemCheckList: ItemCheckList){
        binding.apply {
            viewmodel = viewModel
            item = itemCheckList
            binding.executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): ModifyItemViewHolder{
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = AddModifyChecklistRvItemBinding.inflate(layoutInflater, parent, false)
            return ModifyItemViewHolder(binding)
        }
    }
}