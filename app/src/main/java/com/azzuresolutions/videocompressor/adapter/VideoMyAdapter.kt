package com.azzuresolutions.videocompressor.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azzuresolutions.videocompressor.R
import com.azzuresolutions.videocompressor.databinding.RvVideoMyListBinding
import com.azzuresolutions.videocompressor.model.VideoModel
import com.bumptech.glide.Glide

class VideoMyAdapter(
    private var context: Context? = null,
    private var videoList: List<VideoModel>
) :
    RecyclerView.Adapter<VideoMyAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: RvVideoMyListBinding = RvVideoMyListBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoMyAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.rv_video_my_list, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvVideoName.text = videoList[position].name
        holder.binding.tvVideoSize.text = "Done | " + formatSize(videoList[position].size.toLong())
        Glide.with(context!!)
            .load(videoList[position].uri)
            .centerCrop()
            .into(holder.binding.ivVideoLogo)
        holder.itemView.setOnClickListener {

        }
    }

    fun formatSize(size: Long): String {
        var size = size
        var suffix: String? = null
        if (size >= 1024) {
            suffix = " Bytes"
            size /= 1024
            if (size >= 1024) {
                suffix = " MB"
                size /= 1024
            }
        }
        val resultBuffer = StringBuilder(java.lang.Long.toString(size))
        var commaOffset = resultBuffer.length - 3
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',')
            commaOffset -= 3
        }
        if (suffix != null) resultBuffer.append(suffix)
        return resultBuffer.toString()
    }

    override fun getItemCount(): Int {
        return videoList.size
    }


}