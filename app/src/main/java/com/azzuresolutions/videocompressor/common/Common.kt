package com.azzuresolutions.videocompressor.common

import android.content.Context
import com.azzuresolutions.videocompressor.R
import java.util.concurrent.TimeUnit

class Common {
    companion object {
        fun formatTime(millis: Long, context: Context): String {
            val hours = TimeUnit.MILLISECONDS.toHours(millis) % 24
            val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
            val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60

            return when {
                hours == 0L && minutes == 0L -> String.format(
                    context.resources.getString(R.string.time_minutes_seconds_formatter),
                    minutes,
                    seconds
                )

                hours == 0L && minutes > 0L -> String.format(
                    context.resources.getString(R.string.time_minutes_seconds_formatter),
                    minutes,
                    seconds
                )

                else -> context.resources.getString(
                    R.string.time_hours_minutes_seconds_formatter,
                    hours,
                    minutes,
                    seconds
                )
            }
        }
    }
}