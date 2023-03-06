package com.azzuresolutions.videocompressor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.azzuresolutions.videocompressor.R
import com.azzuresolutions.videocompressor.activity.GalleryFileActivity
import com.azzuresolutions.videocompressor.common.Common
import com.azzuresolutions.videocompressor.databinding.RvVideoListBinding
import com.azzuresolutions.videocompressor.model.VideoModel
import com.bumptech.glide.Glide
import java.util.concurrent.TimeUnit

class VideoAdapter(
    private var context: Context? = null,
    private var videoList: List<VideoModel>
) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: RvVideoListBinding = RvVideoListBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.rv_video_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        GalleryFileActivity.videoList1 = ArrayList()
        holder.binding.tvVideoName.text = videoList[position].name
        holder.binding.tvVideoName.isSelected = true
        Glide.with(context!!)
            .load(videoList[position].uri)
            .centerCrop()
            .into(holder.binding.ivVideo)
        holder.itemView.setOnClickListener {
            if (GalleryFileActivity.videoList1.size <= 0) {
                videoList[position].isSelect = true
                GalleryFileActivity.videoList1.add(videoList[position])
                holder.binding.imgCheck.visibility = View.VISIBLE
            } else if (videoList[position].isSelect) {
                videoList[position].isSelect = false
                GalleryFileActivity.videoList1.remove(videoList[position])
                holder.binding.imgCheck.visibility = View.GONE
            } else {
                Toast.makeText(context, "File is selected", Toast.LENGTH_SHORT).show()
            }
        }
        holder.binding.tvVideo.text =
            Common.formatTime(videoList[position].duration.toLong(), context!!)
    }

}