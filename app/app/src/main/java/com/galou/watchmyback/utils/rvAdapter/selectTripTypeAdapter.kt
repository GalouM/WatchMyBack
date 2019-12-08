package com.galou.watchmyback.utils.rvAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.data.entity.TripType
import com.galou.watchmyback.databinding.TypeTripItemBinding

/**
 * @author galou
 * 2019-11-24
 */

class SelectTripTypeAdapter(
    private val types: List<TripType>,
    private val clickListener: TripTypeSelectionListener
): RecyclerView.Adapter<SelectTripTypeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectTripTypeViewHolder =
        SelectTripTypeViewHolder.from(parent)

    override fun getItemCount(): Int = types.size

    override fun onBindViewHolder(holder: SelectTripTypeViewHolder, position: Int) {
        holder.bindWithType(types[position], clickListener)
    }
}

class SelectTripTypeViewHolder(private val binding: TypeTripItemBinding) : RecyclerView.ViewHolder(binding.root){
    fun bindWithType(tripType: TripType, clickListener: TripTypeSelectionListener){
        with(binding){
            type = tripType
            listener = clickListener
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): SelectTripTypeViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = TypeTripItemBinding.inflate(layoutInflater, parent,false)
            return SelectTripTypeViewHolder(
                binding
            )
        }
    }
}