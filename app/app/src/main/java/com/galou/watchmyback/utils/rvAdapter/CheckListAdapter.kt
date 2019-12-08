package com.galou.watchmyback.utils.rvAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.databinding.CheckListRvItemBinding

/**
 * @author galou
 * 2019-11-20
 */
class CheckListAdapter(
    var checkLists: List<CheckListWithItems>,
    private val checkListListener: CheckListListener
) : RecyclerView.Adapter<CheckListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckListViewHolder =
        CheckListViewHolder.from(
            parent
        )

    override fun getItemCount(): Int = checkLists.size

    override fun onBindViewHolder(holder: CheckListViewHolder, position: Int) {
        holder.bindWithCheckList(checkListListener, checkLists[position])
    }
}

class CheckListViewHolder(private val binding: CheckListRvItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bindWithCheckList(listenerCheckList: CheckListListener, checkListWithItem: CheckListWithItems){
        with(binding){
            checklist = checkListWithItem
            listener = listenerCheckList
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): CheckListViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = CheckListRvItemBinding.inflate(layoutInflater, parent, false)
            return CheckListViewHolder(
                binding
            )
        }
    }

}