package com.galou.watchmyback.utils.rvAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.data.applicationUse.TripDisplay
import com.galou.watchmyback.databinding.TripRvItemBinding
import com.galou.watchmyback.tripsView.TripsViewModel

/**
 * @author galou
 * 2019-12-20
 */

class DisplayTripAdapter(
    var trips: List<TripDisplay>,
    private val viewModel: TripsViewModel
) : RecyclerView.Adapter<DisplayTripViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisplayTripViewHolder =
        DisplayTripViewHolder.from(parent)

    override fun getItemCount(): Int = trips.size

    override fun onBindViewHolder(holder: DisplayTripViewHolder, position: Int) {
        holder.bindWithTrip(trips[position], viewModel)
    }
}

class DisplayTripViewHolder(private val binding: TripRvItemBinding) : RecyclerView.ViewHolder(binding.root){

    fun bindWithTrip(trip: TripDisplay, viewModel: TripsViewModel
    ){
        with(binding){
            this.trip = trip
            viewmodel = viewModel

        }

    }

    companion object {
        fun from(parent: ViewGroup): DisplayTripViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = TripRvItemBinding.inflate(layoutInflater, parent, false)
            return DisplayTripViewHolder(binding)
        }
    }
}