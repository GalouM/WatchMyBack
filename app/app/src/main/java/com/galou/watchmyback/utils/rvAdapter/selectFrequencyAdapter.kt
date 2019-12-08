package com.galou.watchmyback.utils.rvAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.data.entity.TripUpdateFrequency
import com.galou.watchmyback.databinding.UpdateFrequencyItemBinding

/**
 * @author galou
 * 2019-12-03
 */

class SelectFrequencyAdapter(private val frequencies: List<TripUpdateFrequency>,
                             private val clickListener: UpdateFrequencyListener
): RecyclerView.Adapter<SelectUpdateFrequencyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectUpdateFrequencyViewHolder =
        SelectUpdateFrequencyViewHolder.from(parent)

    override fun getItemCount(): Int = frequencies.size

    override fun onBindViewHolder(holder: SelectUpdateFrequencyViewHolder, position: Int) {
        holder.bindWithFrequency(frequencies[position], clickListener)
    }
}

class SelectUpdateFrequencyViewHolder(private val binding: UpdateFrequencyItemBinding) : RecyclerView.ViewHolder(binding.root){
    fun bindWithFrequency(updateFrequency: TripUpdateFrequency, clickListener: UpdateFrequencyListener){
        with(binding){
            frequency = updateFrequency
            listener = clickListener
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): SelectUpdateFrequencyViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = UpdateFrequencyItemBinding.inflate(layoutInflater, parent,false)
            return SelectUpdateFrequencyViewHolder(
                binding
            )
        }
    }
}