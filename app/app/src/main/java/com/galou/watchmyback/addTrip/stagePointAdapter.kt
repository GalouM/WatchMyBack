package com.galou.watchmyback.addTrip

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.data.entity.PointTripWithData
import com.galou.watchmyback.databinding.AddTripStagesRvItemBinding

/**
 * @author galou
 * 2019-12-03
 */

class StagePointAdapter(
    var points: List<PointTripWithData>,
    private val viewModel: AddTripViewModel
) : RecyclerView.Adapter<StagePointViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StagePointViewHolder =
        StagePointViewHolder.from(parent)

    override fun getItemCount(): Int = points.size

    override fun onBindViewHolder(holder: StagePointViewHolder, position: Int) {
        holder.bindWithPoint(viewModel, points[position])
    }
}

class StagePointViewHolder(private val binding: AddTripStagesRvItemBinding) : RecyclerView.ViewHolder(binding.root){
    fun bindWithPoint(viewModel: AddTripViewModel, stagePoint: PointTripWithData){
        with(binding){
            point = stagePoint
            viewmodel = viewModel
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): StagePointViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = AddTripStagesRvItemBinding.inflate(layoutInflater, parent, false)
            return StagePointViewHolder(binding)
        }
    }
}