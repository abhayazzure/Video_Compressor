package com.azzuresolutions.videocompressor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azzuresolutions.videocompressor.R
import com.azzuresolutions.videocompressor.databinding.RvVideoListBinding
import com.azzuresolutions.videocompressor.model.VideoModel
import com.bumptech.glide.Glide

class VideoAdapter(
    private var context: Context? = null,
    private var videoList: List<VideoModel>,
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
        holder.binding.tvVideoName.text = videoList[position].name
        holder.binding.tvVideoName.isSelected = true
        Glide.with(context!!)
            .load(videoList[position].uri)
            .centerCrop()
            .into(holder.binding.ivVideo)
        holder.itemView.setOnClickListener {
            videoList[position].isSelect = true
            if (videoList[position].isSelect) {
                holder.binding.imgCheck.visibility = View.VISIBLE
            } else {
                videoList[position].isSelect = false
                holder.binding.imgCheck.visibility = View.GONE
            }
        }
    }
}