package com.example.runningapp.other

import android.content.Context
import android.icu.util.Calendar
import android.widget.TextView
import com.example.runningapp.R
import com.example.runningapp.db.Run
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView(
    val runs: List<Run>,
    c: Context,
    layoutId: Int
) : MarkerView(c, layoutId) {

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if (e == null) {
            return
        }
        val curRunId = e.x.toInt()
        val run = runs[curRunId]

        val TVdate = findViewById<TextView>(R.id.tvDate)
        val TVavgSpeed = findViewById<TextView>(R.id.tvAvgSpeed)
        val TVdistance = findViewById<TextView>(R.id.tvDistance)
        val TVduration = findViewById<TextView>(R.id.tvDuration)
        val TVcaloriesBurned = findViewById<TextView>(R.id.tvCaloriesBurned)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timestamp // This is the date in millisecond
        }
        val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        TVdate.text = dateFormat.format(calendar.time)

        val avgSpeed = "${run.avgSpeedInKMH}km/h"
        TVavgSpeed.text = avgSpeed

        val distanceInKm = "${run.distanceInMeters / 1000f}km"
        TVdistance.text = distanceInKm

        TVduration.text = TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)

        val caloriesBurned = "${run.caloriesBurned}kcal"
        TVcaloriesBurned.text = caloriesBurned
    }
}