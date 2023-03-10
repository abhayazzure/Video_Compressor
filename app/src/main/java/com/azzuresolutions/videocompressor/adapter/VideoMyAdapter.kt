package com.azzuresolutions.videocompressor.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azzuresolutions.videocompressor.R
import com.azzuresolutions.videocompressor.activity.AudioPlayActivity
import com.azzuresolutions.videocompressor.activity.GalleryFileActivity
import com.azzuresolutions.videocompressor.activity.VideoPlayActivity
import com.azzuresolutions.videocompressor.databinding.RvVideoMyListBinding
import com.azzuresolutions.videocompressor.fragment.AudioFragment
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
        GalleryFileActivity.videoList1 = ArrayList()
        holder.binding.tvVideoName.text = videoList[position].name
        holder.binding.tvVideoSize.text = "Done | " + formatSize(videoList[position].size.toLong())
        Glide.with(context!!)
            .load(videoList[position].uri)
            .centerCrop()
            .into(holder.binding.ivVideoLogo)
        holder.itemView.setOnClickListener {
            if (AudioFragment.click) {
                GalleryFileActivity.videoList1.add(videoList[position])
                context!!.startActivity(
                    Intent(context, AudioPlayActivity::class.java).putExtra(
                        "name",
                        stripExtension(videoList[position].name)
                    )
                )
            } else {
                GalleryFileActivity.videoList1.add(videoList[position])
                context!!.startActivity(
                    Intent(context, VideoPlayActivity::class.java).putExtra(
                        "name",
                        stripExtension(videoList[position].name)
                    )
                )
            }
        }
    }

    fun stripExtension(s: String?): String? {
        return if (s != null && s.lastIndexOf(".") > 0) s.substring(0, s.lastIndexOf(".")) else s
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