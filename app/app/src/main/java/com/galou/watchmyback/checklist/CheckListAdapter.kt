package com.galou.watchmyback.checklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.data.entity.CheckList
import com.galou.watchmyback.databinding.CheckListRvItemBinding

/**
 * @author galou
 * 2019-11-20
 */
class CheckListAdapter(
    var checkLists: List<CheckList>,
    private val checkListViewModel: CheckListViewModel
) : RecyclerView.Adapter<CheckListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckListViewHolder =
        CheckListViewHolder.from(parent)

    override fun getItemCount(): Int = checkLists.size

    override fun onBindViewHolder(holder: CheckListViewHolder, position: Int) {
        holder.bindWithCheckList(checkListViewModel, checkLists[position])
    }
}

class CheckListViewHolder(private val binding: CheckListRvItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bindWithCheckList(viewModel: CheckListViewModel, checkList: CheckList){
        binding.checklist = checkList
        binding.viewmodel = viewModel
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): CheckListViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = CheckListRvItemBinding.inflate(layoutInflater, parent, false)
            return CheckListViewHolder(binding)
        }
    }

}