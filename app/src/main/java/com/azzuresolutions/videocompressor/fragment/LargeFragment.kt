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
import com.azzuresolutions.videocompressor.adapter.VideoAdapter
import com.azzuresolutions.videocompressor.databinding.FragmentLargeBinding
import com.azzuresolutions.videocompressor.model.VideoModel

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
            MediaStore.Video.Media.SIZE
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

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val duration = cursor.getInt(durationColumn)
                val size = cursor.getInt(sizeColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id
                )
                if (size / 1024 >= 102400) {
                    videoList.add(VideoModel(contentUri, name, duration, size, false))
                }
            }
        }
        return videoList
    }

}