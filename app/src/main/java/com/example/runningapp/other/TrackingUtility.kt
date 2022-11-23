package com.example.runningapp.other

import android.Manifest
import android.content.Context
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit

object TrackingUtility {
    fun hasLocationPermission (context: Context) =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )

    // include millis is used because in tracking fragment we want those millis but in our notification we don't need the millisecond
    fun getFormattedStopWatchTime( ms:Long, includeMillis: Boolean = false): String{
        var millisecondds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(millisecondds)
        millisecondds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisecondds)
        millisecondds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisecondds)
        if (!includeMillis){
            return "${if(hours<10) "0" else ""}$hours:" +
                    "${if(minutes<10) "0" else ""}$minutes:" +
                    "${if(seconds<10) "0" else ""}$seconds"
        }
        millisecondds -= TimeUnit.SECONDS.toMillis(seconds)
        millisecondds /= 10 // because we only want a 2 digit number and not a 3 digit number
        return "${if(hours<10) "0" else ""}$hours:" +
                "${if(minutes<10) "0" else ""}$minutes:" +
                "${if(seconds<10) "0" else ""}$seconds:" +
                "${if(millisecondds<10) "0" else ""}$millisecondds"

    }
}