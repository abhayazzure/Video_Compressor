package com.azzuresolutions.videocompressor.fragment

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.azzuresolutions.videocompressor.R
import com.azzuresolutions.videocompressor.adapter.VideoAdapter
import com.azzuresolutions.videocompressor.databinding.FragmentLargeBinding
import com.azzuresolutions.videocompressor.model.VideoModel
import java.util.concurrent.TimeUnit

class LargeFragment : Fragment() {
    private lateinit var binding: FragmentLargeBinding
    var videoList = mutableListOf<VideoModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLargeBinding.inflate(layoutInflater, container, false)
        setAdapter()
        return binding.root
    }

    private fun setAdapter() {
        videoList = getAllMediaFilesOnDevice()
        this.binding.rvVideo.layoutManager = GridLayoutManager(requireContext(), 3)
        val videoAdapter = VideoAdapter(requireContext(), videoList)
        this.binding.rvVideo.adapter = videoAdapter
    }

    private fun getAllMediaFilesOnDevice(): MutableList<VideoModel> {
        val videoList = mutableListOf<VideoModel>()
        val collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT
        )

        val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

        val query: Cursor = requireActivity().contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )!!

        query.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val videoWidth = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
            val videoHeight = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val duration = cursor.getInt(durationColumn)
                val size = cursor.getInt(sizeColumn)
                val width = cursor.getInt(videoWidth)
                val height = cursor.getInt(videoHeight)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id
                )

                if (formatTime(duration.toLong()) >= String.format(
                        resources.getString(R.string.time_minutes_seconds_formatter),
                        300000, 30000
                    )
                ) {
                    videoList.add(VideoModel(contentUri, name, duration,width,height, size, false))
                }
            }
        }
        return videoList
    }

    private fun formatTime(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60

        return when {
            hours == 0L && minutes == 0L -> String.format(
                resources.getString(R.string.time_minutes_seconds_formatter),
                minutes,
                seconds
            )

            hours == 0L && minutes > 0L -> String.format(
                resources.getString(R.string.time_minutes_seconds_formatter),
                minutes,
                seconds
            )

            else -> resources.getString(
                R.string.time_hours_minutes_seconds_formatter,
                hours,
                minutes,
                seconds
            )
        }
    }
}