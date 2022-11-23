package com.example.runningapp.adapters

import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.runningapp.databinding.ItemRunBinding
import com.example.runningapp.db.Run
import com.example.runningapp.other.TrackingUtility
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {
    inner class RunViewHolder(val binding : ItemRunBinding) :  RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val layoutInflater = LayoutInflater.from (parent.context)
        val binding = ItemRunBinding.inflate(layoutInflater,parent,false)
        return RunViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]
        holder.binding.apply {
            Glide.with(holder.itemView).load(run.img).into(ivRunImage)
            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timestamp // This is the date in millisecond
            }
            val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            tvDate.text = dateFormat.format(calendar.time)

            val avgSpeed = "${run.avgSpeedInKMH}km/h"
            tvAvgSpeed.text = avgSpeed

            val distanceInKm = "${run.distanceInMeters / 1000f}km"
            tvDistance.text = distanceInKm

            tvTime.text = TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)

            val caloriesBurned = "${run.caloriesBurned}kcal"
            tvCalories.text = caloriesBurned
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    val diffCallBack = object : DiffUtil.ItemCallback<Run>(){
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode() // if hashcode is the same, every single element of these items must be the same
        }
    }
    val differ = AsyncListDiffer(this,diffCallBack)

    fun submitList (list : List<Run>) = differ.submitList(list)
}