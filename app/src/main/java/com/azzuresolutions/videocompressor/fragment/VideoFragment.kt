package com.azzuresolutions.videocompressor.fragment

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.azzuresolutions.videocompressor.adapter.VideoMyAdapter
import com.azzuresolutions.videocompressor.databinding.FragmentVideoBinding
import com.azzuresolutions.videocompressor.model.VideoModel

class VideoFragment : Fragment() {
    private lateinit var binding: FragmentVideoBinding
    var videoList = mutableListOf<VideoModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVideoBinding.inflate(layoutInflater, container, false)
        setAdapter()
        return binding.root
    }

    private fun setAdapter() {
        videoList = getAllMediaFilesOnDevice()
        this.binding.rvVideo.layoutManager = LinearLayoutManager(requireContext())
        val videoAdapter = VideoMyAdapter(requireContext(), videoList)
        this.binding.rvVideo.adapter = videoAdapter
    }

    private fun getAllMediaFilesOnDevice(): MutableList<VideoModel> {
        val videoList = mutableListOf<VideoModel>()
        val collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val path = Environment.DIRECTORY_DOWNLOADS + "/Video Compressor"
        val strArr = arrayOf("%$path%")
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
            "relative_path like ? ",
            strArr,
            sortOrder
        )!!

        query.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val videoWidth = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
            val videoHeight = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val duration = cursor.getInt(durationColumn)
                val size = cursor.getInt(sizeColumn)
                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id
                )
                val width = cursor.getInt(videoWidth)
                val height = cursor.getInt(videoHeight)
                videoList.add(
                    VideoModel(
                        contentUri,
                        name,
                        duration,
                        width,
                        height,
                        size,
                        false
                    )
                )
            }
        }

        return videoList
    }
}